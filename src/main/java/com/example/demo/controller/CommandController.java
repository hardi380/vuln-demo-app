package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Command-injection-vulnerable endpoint.
 * User input is concatenated directly into a shell command without sanitization.
 * IBM Concert SAST / Exposure scan target.
 */
@RestController
@RequestMapping("/api")
public class CommandController {

    @GetMapping("/ping")
    public ResponseEntity<String> ping(@RequestParam String host) {
        try {
            // VULNERABLE: user-controlled input concatenated into exec() without any sanitization
            String command = "ping -c 1 " + host;
            Process process = Runtime.getRuntime().exec(command);
            String output = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            ).lines().collect(Collectors.joining("\n"));
            return ResponseEntity.ok(output);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
