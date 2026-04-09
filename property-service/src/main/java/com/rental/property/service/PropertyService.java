package com.rental.property.service;

import com.rental.property.dto.PropertyDTO;
import com.rental.property.dto.PropertyRequestDTO;
import com.rental.property.model.Property;
import com.rental.property.model.PropertyType;
import com.rental.property.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public List<PropertyDTO> search(String city, String type,
                                    BigDecimal minPrice, BigDecimal maxPrice,
                                    Integer rooms) {
        PropertyType propertyType = (type != null && !type.isBlank())
                ? PropertyType.valueOf(type.toUpperCase())
                : null;

        return propertyRepository
                .search(city, propertyType, minPrice, maxPrice, rooms)
                .stream()
                .map(PropertyDTO::from)
                .collect(Collectors.toList());
    }

    public List<PropertyDTO> getAll() {
        return propertyRepository.findByAvailableTrue()
                .stream()
                .map(PropertyDTO::from)
                .collect(Collectors.toList());
    }

    public PropertyDTO getById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        return PropertyDTO.from(property);
    }

    public List<PropertyDTO> getByOwner(Long ownerId) {
        return propertyRepository.findByOwnerId(ownerId)
                .stream()
                .map(PropertyDTO::from)
                .collect(Collectors.toList());
    }

    public PropertyDTO create(PropertyRequestDTO request, Long ownerId) {
        Property property = new Property();
        mapRequestToProperty(request, property);
        property.setOwnerId(ownerId);
        return PropertyDTO.from(propertyRepository.save(property));
    }

    public PropertyDTO update(Long id, PropertyRequestDTO request, Long ownerId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.getOwnerId().equals(ownerId)) {
            throw new IllegalStateException("You are not the owner of this property");
        }

        mapRequestToProperty(request, property);
        return PropertyDTO.from(propertyRepository.save(property));
    }

    public void delete(Long id, Long ownerId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.getOwnerId().equals(ownerId)) {
            throw new IllegalStateException("You are not the owner of this property");
        }

        propertyRepository.delete(property);
    }

    public void updateAvailability(Long id, Boolean available) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        property.setAvailable(available);
        propertyRepository.save(property);
    }

    private void mapRequestToProperty(PropertyRequestDTO request, Property property) {
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setType(PropertyType.valueOf(request.getType().toUpperCase()));
        property.setCity(request.getCity());
        property.setAddress(request.getAddress());
        property.setPricePerMonth(request.getPricePerMonth());
        property.setSurface(request.getSurface());
        property.setRooms(request.getRooms());
        property.setImageUrl(request.getImageUrl());
    }
}
