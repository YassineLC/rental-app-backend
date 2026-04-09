package com.rental.property.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/headers")
    public ResponseEntity<Map<String, String>> getHeaders(
            @RequestHeader(name = "X-User-Id", required = false) String userId,
            @RequestHeader(name = "X-User-Role", required = false) String role,
            @RequestHeader(name = "Authorization", required = false) String auth) {

        Map<String, String> response = new HashMap<>();
        response.put("X-User-Id", userId != null ? userId : "NOT FOUND");
        response.put("X-User-Role", role != null ? role : "NOT FOUND");
        response.put("Authorization", auth != null ? "Bearer token present" : "NO TOKEN");
        return ResponseEntity.ok(response);
    }
}
