package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;

/**
 * Insecure deserialization endpoint.
 * Deserializes caller-supplied Base64-encoded Java objects without any type filter.
 * IBM Concert SAST / Exposure scan target.
 */
@RestController
@RequestMapping("/api")
public class DeserializeController {

    @PostMapping(value = "/deserialize", consumes = "text/plain", produces = "text/plain")
    public ResponseEntity<String> deserialize(@RequestBody String base64Input) {
        try {
            byte[] data = Base64.getDecoder().decode(base64Input.trim());
            // VULNERABLE: no ObjectInputFilter, accepts any serialized type
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                Object obj = ois.readObject();
                return ResponseEntity.ok("Deserialized: " + obj.toString());
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
