package com.rental.booking.service;

import com.rental.booking.client.PropertyClient;
import com.rental.booking.client.PropertyResponse;
import com.rental.booking.dto.BookingDTO;
import com.rental.booking.dto.BookingRequestDTO;
import com.rental.booking.model.Booking;
import com.rental.booking.model.BookingStatus;
import com.rental.booking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyClient propertyClient;

    public BookingService(BookingRepository bookingRepository, PropertyClient propertyClient) {
        this.bookingRepository = bookingRepository;
        this.propertyClient = propertyClient;
    }

    public BookingDTO create(BookingRequestDTO request, Long tenantId) {
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        PropertyResponse property = propertyClient.getPropertyById(request.getPropertyId());

        if (!property.getAvailable()) {
            throw new IllegalStateException("Property is not available");
        }

        boolean overlapping = bookingRepository.existsOverlappingBooking(
                request.getPropertyId(), request.getStartDate(), request.getEndDate());
        if (overlapping) {
            throw new IllegalStateException("Property is already booked for these dates");
        }

        long nights = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        // Price is per month; calculate proportional cost
        BigDecimal totalPrice = property.getPricePerMonth()
                .multiply(BigDecimal.valueOf(nights))
                .divide(BigDecimal.valueOf(30), 2, java.math.RoundingMode.HALF_UP);

        Booking booking = new Booking();
        booking.setPropertyId(request.getPropertyId());
        booking.setTenantId(tenantId);
        booking.setOwnerId(property.getOwnerId());
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setTotalPrice(totalPrice);
        booking.setMessage(request.getMessage());
        booking.setPropertyTitle(property.getTitle());
        booking.setPropertyCity(property.getCity());

        return BookingDTO.from(bookingRepository.save(booking));
    }

    public BookingDTO getById(Long id, Long userId) {
        Booking booking = findById(id);
        if (!booking.getTenantId().equals(userId) && !booking.getOwnerId().equals(userId)) {
            throw new IllegalStateException("Access denied");
        }
        return BookingDTO.from(booking);
    }

    public List<BookingDTO> getByTenant(Long tenantId) {
        return bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId)
                .stream().map(BookingDTO::from).collect(Collectors.toList());
    }

    public List<BookingDTO> getByOwner(Long ownerId) {
        return bookingRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId)
                .stream().map(BookingDTO::from).collect(Collectors.toList());
    }

    public BookingDTO confirm(Long id, Long ownerId) {
        Booking booking = findById(id);
        if (!booking.getOwnerId().equals(ownerId)) {
            throw new IllegalStateException("Only the owner can confirm this booking");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return BookingDTO.from(bookingRepository.save(booking));
    }

    public BookingDTO cancel(Long id, Long userId) {
        Booking booking = findById(id);
        if (!booking.getTenantId().equals(userId) && !booking.getOwnerId().equals(userId)) {
            throw new IllegalStateException("Access denied");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED
                || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking cannot be cancelled");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        // Mark property as available again
        propertyClient.updateAvailability(booking.getPropertyId(), true);
        return BookingDTO.from(bookingRepository.save(booking));
    }

    private Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }
}
