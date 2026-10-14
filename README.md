# vuln-demo-app

> ⚠️ **This application is intentionally insecure.**  
> It exists solely to demonstrate IBM Concert vulnerability scanning capabilities.  
> **Do NOT deploy in any real or production environment.**

---

## Prerequisites

| Requirement | Version |
|---|---|
| Java | 11+ (17 empfohlen) |
| Maven | 3.8+ |
| Apache Tomcat | 9.0.x |

---

## Build

```bash
cd vuln-demo-app
mvn clean package -DskipTests
```

The WAR file is produced at:

```
target/vuln-demo-app-1.0.0.war
```

---

## Deploy on Tomcat

1. **Copy the WAR** to Tomcat's webapps directory:

   ```bash
   cp target/vuln-demo-app-1.0.0.war $CATALINA_HOME/webapps/
   ```

2. **Start Tomcat** (if not already running):

   ```bash
   $CATALINA_HOME/bin/startup.sh
   ```

   On Windows:

   ```bat
   %CATALINA_HOME%\bin\startup.bat
   ```

3. **Verify** — Tomcat auto-deploys the WAR. The context root matches the WAR filename:

   ```
   http://localhost:8080/vuln-demo-app-1.0.0/api/config
   ```

   > **Tip:** rename the WAR to `demo.war` before copying for a shorter context root:
   > `http://localhost:8080/demo/api/config`

---

## Endpoints

Base URL: `http://localhost:8080/demo` (assuming WAR renamed to `demo.war`)

### `GET /api/config` — Hardcoded Secrets

Returns fake config metadata. Source contains hardcoded AWS keys, GitHub token, and RSA key stub.

```bash
curl http://localhost:8080/demo/api/config
```

---

### `POST /api/parse-xml` — XXE (XML External Entity)

Parses XML without secure processing flags — vulnerable to XXE.

```bash
curl -X POST http://localhost:8080/demo/api/parse-xml \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0"?><root><item>hello</item></root>'
```

XXE payload example (reads `/etc/passwd` on Linux):

```bash
curl -X POST http://localhost:8080/demo/api/parse-xml \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0"?><!DOCTYPE foo [<!ENTITY xxe SYSTEM "file:///etc/passwd">]><root>&xxe;</root>'
```

---

### `GET /api/fetch` — SSRF (Server-Side Request Forgery)

Fetches any URL supplied by the caller without validation.

```bash
curl "http://localhost:8080/demo/api/fetch?url=http://example.com"
```

Internal metadata example (cloud environment):

```bash
curl "http://localhost:8080/demo/api/fetch?url=http://169.254.169.254/latest/meta-data/"
```

---

### `GET /api/ping` — Command Injection

Executes `ping -c 1 <host>` with unsanitized user input.

```bash
curl "http://localhost:8080/demo/api/ping?host=127.0.0.1"
```

Injection example:

```bash
curl "http://localhost:8080/demo/api/ping?host=127.0.0.1;whoami"
```

---

### `POST /api/deserialize` — Insecure Deserialization

Deserializes a Base64-encoded Java serialized object without type filtering.

```bash
# Serialize a simple String object and send it
PAYLOAD=$(python3 -c "
import base64, struct
# Java serialized String 'hello' (simplified demo payload)
data = bytes([0xac, 0xed, 0x00, 0x05, 0x74, 0x00, 0x05, 0x68, 0x65, 0x6c, 0x6c, 0x6f])
print(base64.b64encode(data).decode())
")
curl -X POST http://localhost:8080/demo/api/deserialize \
  -H "Content-Type: text/plain" \
  -d "$PAYLOAD"
```

---

## Intentional Vulnerabilities (IBM Concert Demo Coverage)

| Category | Finding | Location |
|---|---|---|
| **Secrets** | Hardcoded API key, JWT token, DB password | `application.properties` |
| **Secrets** | Hardcoded AWS keys, GitHub token, RSA private key | `ConfigService.java` |
| **Package CVE** | `jackson-databind` 2.13.0 — CVE-2022-42003, CVE-2022-42004 | `pom.xml` |
| **Package CVE** | `snakeyaml` 1.29 — CVE-2022-25857, CVE-2022-38750 | `pom.xml` |
| **Package CVE** | `commons-text` 1.9 — CVE-2022-42889 (Text4Shell) | `pom.xml` |
| **Package CVE** | `tomcat-embed-core` 9.0.62 — multiple CVEs | `pom.xml` |
| **Exposure / SAST** | XXE — `DocumentBuilderFactory` without secure features | `XmlController.java` |
| **Exposure / SAST** | SSRF — `new URL(url).openStream()` without host validation | `SsrfController.java` |
| **Exposure / SAST** | Command Injection — `Runtime.exec()` with unsanitized input | `CommandController.java` |
| **Exposure / SAST** | Insecure Deserialization — `ObjectInputStream` without filter | `DeserializeController.java` |

---

## Stop Tomcat

```bash
$CATALINA_HOME/bin/shutdown.sh
```
