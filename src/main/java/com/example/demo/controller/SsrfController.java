package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * SSRF-vulnerable endpoint.
 * Fetches any caller-supplied URL without host validation or allowlist.
 * IBM Concert SAST / Exposure scan target.
 */
@RestController
@RequestMapping("/api")
public class SsrfController {

    @GetMapping("/fetch")
    public ResponseEntity<String> fetch(@RequestParam String url) {
        try {
            // VULNERABLE: no host validation, no URL scheme restriction
            try (InputStream in = new URL(url).openStream()) {
                String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                return ResponseEntity.ok(body);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
