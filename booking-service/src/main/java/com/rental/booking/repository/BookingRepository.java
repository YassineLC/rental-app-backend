package com.rental.booking.repository;

import com.rental.booking.model.Booking;
import com.rental.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    List<Booking> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<Booking> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.propertyId = :propertyId
              AND b.status IN ('PENDING', 'CONFIRMED')
              AND b.startDate < :endDate
              AND b.endDate > :startDate
            """)
    boolean existsOverlappingBooking(
            @Param("propertyId") Long propertyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
