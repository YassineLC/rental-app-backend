package com.rental.booking.controller;

import com.rental.booking.dto.BookingDTO;
import com.rental.booking.dto.BookingRequestDTO;
import com.rental.booking.dto.UnavailablePeriod;
import com.rental.booking.security.JwtTokenProvider;
import com.rental.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final JwtTokenProvider jwtTokenProvider;

    public BookingController(BookingService bookingService, JwtTokenProvider jwtTokenProvider) {
        this.bookingService = bookingService;
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

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody BookingRequestDTO request) {

        Long tenantId = extractUserIdFromAuth(authorization);
        String role = extractRoleFromAuth(authorization);

        if (tenantId == null || !"TENANT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        BookingDTO booking = bookingService.create(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        Long userId = extractUserIdFromAuth(authorization);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(bookingService.getById(id, userId));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingDTO>> getMyBookings(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        Long tenantId = extractUserIdFromAuth(authorization);
        if (tenantId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(bookingService.getByTenant(tenantId));
    }

    @GetMapping("/owner/requests")
    public ResponseEntity<List<BookingDTO>> getOwnerBookings(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        Long ownerId = extractUserIdFromAuth(authorization);
        String role = extractRoleFromAuth(authorization);

        if (ownerId == null || (!"OWNER".equals(role) && !"ADMIN".equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(bookingService.getByOwner(ownerId));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingDTO> confirmBooking(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        Long ownerId = extractUserIdFromAuth(authorization);
        String role = extractRoleFromAuth(authorization);

        if (ownerId == null || (!"OWNER".equals(role) && !"ADMIN".equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(bookingService.confirm(id, ownerId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        Long userId = extractUserIdFromAuth(authorization);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(bookingService.cancel(id, userId));
    }

    @GetMapping("/property/{propertyId}/unavailable")
    public ResponseEntity<List<UnavailablePeriod>> getUnavailablePeriods(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(bookingService.getUnavailablePeriods(propertyId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
}
