package com.rental.booking.dto;

import com.rental.booking.model.Booking;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDTO {

    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private String propertyCity;
    private Long tenantId;
    private Long ownerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private BigDecimal totalPrice;
    private String message;
    private LocalDateTime createdAt;

    public static BookingDTO from(Booking b) {
        BookingDTO dto = new BookingDTO();
        dto.id = b.getId();
        dto.propertyId = b.getPropertyId();
        dto.propertyTitle = b.getPropertyTitle();
        dto.propertyCity = b.getPropertyCity();
        dto.tenantId = b.getTenantId();
        dto.ownerId = b.getOwnerId();
        dto.startDate = b.getStartDate();
        dto.endDate = b.getEndDate();
        dto.status = b.getStatus().name();
        dto.totalPrice = b.getTotalPrice();
        dto.message = b.getMessage();
        dto.createdAt = b.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public Long getPropertyId() { return propertyId; }
    public String getPropertyTitle() { return propertyTitle; }
    public String getPropertyCity() { return propertyCity; }
    public Long getTenantId() { return tenantId; }
    public Long getOwnerId() { return ownerId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
