package com.rental.property.controller;

import com.rental.property.dto.PropertyDTO;
import com.rental.property.dto.PropertyRequestDTO;
import com.rental.property.security.JwtTokenProvider;
import com.rental.property.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;
    private final JwtTokenProvider jwtTokenProvider;

    public PropertyController(PropertyService propertyService, JwtTokenProvider jwtTokenProvider) {
        this.propertyService = propertyService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private Long extractUserIdFromAuth(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
                return jwtTokenProvider.extractUserId(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String extractRoleFromAuth(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
                return jwtTokenProvider.extractRole(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<PropertyDTO>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer rooms) {

        boolean hasFilters = city != null || type != null
                || minPrice != null || maxPrice != null || rooms != null;

        List<PropertyDTO> result = hasFilters
                ? propertyService.search(city, type, minPrice, maxPrice, rooms)
                : propertyService.getAll();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getById(id));
    }

    @GetMapping("/owner/me")
    public ResponseEntity<List<PropertyDTO>> getMyProperties(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        Long ownerId = extractUserIdFromAuth(authorization);
        String role = extractRoleFromAuth(authorization);

        if (ownerId == null || (!"OWNER".equals(role) && !"ADMIN".equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(propertyService.getByOwner(ownerId));
    }

    @PostMapping
    public ResponseEntity<PropertyDTO> createProperty(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody PropertyRequestDTO request) {

        Long ownerId = extractUserIdFromAuth(authorization);
        String role = extractRoleFromAuth(authorization);

        if (ownerId == null || (!"OWNER".equals(role) && !"ADMIN".equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        PropertyDTO created = propertyService.create(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long ownerId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody PropertyRequestDTO request) {

        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(propertyService.update(id, request, ownerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long ownerId,
            @RequestHeader("X-User-Role") String role) {

        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        propertyService.delete(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    // Internal endpoint called by booking-service to update availability
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable Long id,
            @RequestParam Boolean available) {
        propertyService.updateAvailability(id, available);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
    }
}
