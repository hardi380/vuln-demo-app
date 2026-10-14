package com.example.demo.controller;

import com.example.demo.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes (fake) config info — backed by ConfigService which holds hardcoded secrets.
 */
@RestController
@RequestMapping("/api")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping("/config")
    public ResponseEntity<String> getConfig() {
        return ResponseEntity.ok(configService.getSummary());
    }
}
