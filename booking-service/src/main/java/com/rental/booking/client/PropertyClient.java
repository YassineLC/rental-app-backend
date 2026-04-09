package com.rental.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "property-service")
public interface PropertyClient {

    @GetMapping("/api/properties/{id}")
    PropertyResponse getPropertyById(@PathVariable Long id);

    @PatchMapping("/api/properties/{id}/availability")
    void updateAvailability(@PathVariable Long id, @RequestParam Boolean available);
}
