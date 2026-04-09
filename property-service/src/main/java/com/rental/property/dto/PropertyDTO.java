package com.rental.property.dto;

import com.rental.property.model.Property;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PropertyDTO {

    private Long id;
    private String title;
    private String description;
    private String type;
    private String city;
    private String address;
    private BigDecimal pricePerMonth;
    private Double surface;
    private Integer rooms;
    private Boolean available;
    private Long ownerId;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static PropertyDTO from(Property p) {
        PropertyDTO dto = new PropertyDTO();
        dto.id = p.getId();
        dto.title = p.getTitle();
        dto.description = p.getDescription();
        dto.type = p.getType().name();
        dto.city = p.getCity();
        dto.address = p.getAddress();
        dto.pricePerMonth = p.getPricePerMonth();
        dto.surface = p.getSurface();
        dto.rooms = p.getRooms();
        dto.available = p.getAvailable();
        dto.ownerId = p.getOwnerId();
        dto.imageUrl = p.getImageUrl();
        dto.createdAt = p.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public BigDecimal getPricePerMonth() { return pricePerMonth; }
    public Double getSurface() { return surface; }
    public Integer getRooms() { return rooms; }
    public Boolean getAvailable() { return available; }
    public Long getOwnerId() { return ownerId; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
