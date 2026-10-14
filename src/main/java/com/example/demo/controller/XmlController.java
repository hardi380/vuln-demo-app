package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * XXE-vulnerable XML parsing endpoint.
 * DocumentBuilderFactory is intentionally left without secure-processing features.
 * IBM Concert SAST / Exposure scan target.
 */
@RestController
@RequestMapping("/api")
public class XmlController {

    @PostMapping(value = "/parse-xml", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String parseXml(@RequestBody String xmlBody) throws Exception {
        // VULNERABLE: no setFeature("http://xml.org/sax/features/external-general-entities", false)
        // and no setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(
            new ByteArrayInputStream(xmlBody.getBytes(StandardCharsets.UTF_8))
        );
        return "Root element: " + doc.getDocumentElement().getNodeName();
    }
}
