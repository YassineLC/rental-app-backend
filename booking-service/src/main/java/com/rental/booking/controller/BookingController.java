package com.rental.booking.controller;

import com.rental.booking.dto.BookingDTO;
import com.rental.booking.dto.BookingRequestDTO;
import com.rental.booking.dto.UnavailablePeriod;
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

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(
            @RequestHeader(value = "X-User-Id", required = false) Long tenantId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @Valid @RequestBody BookingRequestDTO request) {

        if (tenantId == null || !"TENANT".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(request, tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(bookingService.getById(id, userId));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingDTO>> getMyBookings(
            @RequestHeader(value = "X-User-Id", required = false) Long tenantId) {

        if (tenantId == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(bookingService.getByTenant(tenantId));
    }

    @GetMapping("/owner/requests")
    public ResponseEntity<List<BookingDTO>> getOwnerBookings(
            @RequestHeader(value = "X-User-Id", required = false) Long ownerId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (ownerId == null || (!"OWNER".equals(role) && !"ADMIN".equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(bookingService.getByOwner(ownerId));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingDTO> confirmBooking(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long ownerId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (ownerId == null || (!"OWNER".equals(role) && !"ADMIN".equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(bookingService.confirm(id, ownerId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(bookingService.cancel(id, userId));
    }

    @GetMapping("/property/{propertyId}/unavailable")
    public ResponseEntity<List<UnavailablePeriod>> getUnavailablePeriods(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(bookingService.getUnavailablePeriods(propertyId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        bookingService.delete(id, userId);
        return ResponseEntity.noContent().build();
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
