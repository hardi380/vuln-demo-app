package com.example.demo.service;

import org.springframework.stereotype.Service;

/**
 * Service class intentionally containing hardcoded credentials.
 * IBM Concert Secrets scan target — covers AWS, GitHub, RSA key patterns.
 */
@Service
public class ConfigService {

    // HARDCODED SECRET: AWS Access Key — CVE pattern / Concert Secrets scan
    private static final String AWS_ACCESS_KEY_ID = "AKIAIOSFODNN7EXAMPLE";
    private static final String AWS_SECRET_ACCESS_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

    // HARDCODED SECRET: GitHub Personal Access Token
    private static final String GITHUB_TOKEN = "ghp_hardcodedTokenForDemoApp1234567890";

    // HARDCODED SECRET: RSA Private Key stub
    private static final String RSA_PRIVATE_KEY =
        "-----BEGIN RSA PRIVATE KEY-----\n" +
        "MIIEowIBAAKCAQEA2a2rwplBQLF29amygykEMmYz0+Kcj3bKBp29K2rFeFsxDER\n" +
        "DEMO_ONLY_NOT_A_REAL_KEY_DO_NOT_USE_IN_PRODUCTION\n" +
        "-----END RSA PRIVATE KEY-----";

    public String getAwsKeyId() {
        return AWS_ACCESS_KEY_ID;
    }

    public String getAwsSecretKey() {
        return AWS_SECRET_ACCESS_KEY;
    }

    public String getGithubToken() {
        return GITHUB_TOKEN;
    }

    public String getRsaPrivateKey() {
        return RSA_PRIVATE_KEY;
    }

    public String getSummary() {
        return String.format(
            "{\"awsKeyId\":\"%s\", \"githubToken\":\"%s\", \"note\":\"demo only\"}",
            AWS_ACCESS_KEY_ID, GITHUB_TOKEN
        );
    }
}
