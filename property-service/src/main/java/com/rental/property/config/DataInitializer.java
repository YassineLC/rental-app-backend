package com.rental.property.config;

import com.rental.property.model.Property;
import com.rental.property.model.PropertyType;
import com.rental.property.repository.PropertyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PropertyRepository propertyRepository;

    public DataInitializer(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public void run(String... args) {
        if (propertyRepository.count() > 0) return;

        // ownerId=1 corresponds to the demo owner created by the user-service DataInitializer
        long ownerId = 1L;

        Property p1 = new Property();
        p1.setTitle("Appartement lumineux Haussmannien — République");
        p1.setDescription(
            "Magnifique appartement de 65 m² au cœur du 11e arrondissement. " +
            "Parquet en chêne, moulures d'époque, cuisine entièrement équipée. " +
            "Proche métro République (lignes 3, 5, 8, 9, 11), commerces et restaurants à pied."
        );
        p1.setType(PropertyType.APARTMENT);
        p1.setCity("Paris");
        p1.setAddress("18 rue du Faubourg du Temple, 75011 Paris");
        p1.setPricePerMonth(new BigDecimal("1650.00"));
        p1.setSurface(65.0);
        p1.setRooms(3);
        p1.setAvailable(true);
        p1.setOwnerId(ownerId);
        p1.setImageUrl(
            "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2" +
            "?w=800&q=80&fit=crop&auto=format"
        );

        Property p2 = new Property();
        p2.setTitle("Maison avec jardin — Quartier Brotteaux");
        p2.setDescription(
            "Belle maison de ville de 120 m² avec jardin privatif de 80 m². " +
            "4 chambres, double salon, cuisine ouverte. Garage individuel inclus. " +
            "Secteur calme et résidentiel, à 10 min à vélo du centre-ville de Lyon."
        );
        p2.setType(PropertyType.HOUSE);
        p2.setCity("Lyon");
        p2.setAddress("7 avenue du Maréchal de Saxe, 69003 Lyon");
        p2.setPricePerMonth(new BigDecimal("2200.00"));
        p2.setSurface(120.0);
        p2.setRooms(5);
        p2.setAvailable(true);
        p2.setOwnerId(ownerId);
        p2.setImageUrl(
            "https://images.unsplash.com/photo-1568605114967-8130f3a36994" +
            "?w=800&q=80&fit=crop&auto=format"
        );

        Property p3 = new Property();
        p3.setTitle("Loft industriel rénové — Chartrons");
        p3.setDescription(
            "Superbe loft de 80 m² dans un ancien entrepôt réhabilité. " +
            "Hauteur sous plafond de 4 m, grandes baies vitrées, cuisine américaine. " +
            "Idéalement situé dans le quartier tendance des Chartrons, à 5 min à pied des quais."
        );
        p3.setType(PropertyType.LOFT);
        p3.setCity("Bordeaux");
        p3.setAddress("32 rue Notre-Dame, 33300 Bordeaux");
        p3.setPricePerMonth(new BigDecimal("1400.00"));
        p3.setSurface(80.0);
        p3.setRooms(2);
        p3.setAvailable(true);
        p3.setOwnerId(ownerId);
        p3.setImageUrl(
            "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688" +
            "?w=800&q=80&fit=crop&auto=format"
        );

        propertyRepository.save(p1);
        propertyRepository.save(p2);
        propertyRepository.save(p3);
    }
}
