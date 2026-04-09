package com.rental.property.repository;

import com.rental.property.model.Property;
import com.rental.property.model.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwnerId(Long ownerId);

    List<Property> findByAvailableTrue();

    @Query("""
            SELECT p FROM Property p
            WHERE p.available = true
              AND (:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')))
              AND (:type IS NULL OR p.type = :type)
              AND (:minPrice IS NULL OR p.pricePerMonth >= :minPrice)
              AND (:maxPrice IS NULL OR p.pricePerMonth <= :maxPrice)
              AND (:rooms IS NULL OR p.rooms >= :rooms)
            ORDER BY p.createdAt DESC
            """)
    List<Property> search(
            @Param("city") String city,
            @Param("type") PropertyType type,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("rooms") Integer rooms
    );
}
