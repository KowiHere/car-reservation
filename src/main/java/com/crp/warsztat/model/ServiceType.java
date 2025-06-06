package com.crp.warsztat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Klasa ServiceType (typ usługi)
 * Opisuje pojedynczy rodzaj usługi oferowanej w warsztacie,
 * np. wymiana oleju, lift kit, przegląd techniczny itd.
 */
@Entity
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unikalny numer usługi (nadawany przez bazę)

    private String name;        // Nazwa usługi (np. "Lift kit", "Diagnostyka")
    private String description; // Opis usługi (opcjonalnie, np. co obejmuje)
    private Integer durationMinutes; // Czas trwania usługi w minutach (np. 30, 180, 360)
    private String category;    // Kategoria usługi (np. "Naprawy", "Modernizacje", "Przeglądy")

    // --- Konstruktory ---

    public ServiceType() {
        // Bezparametrowy konstruktor wymagany przez JPA
    }

    public ServiceType(String name, String description, Integer durationMinutes, String category) {
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.category = category;
    }

    // --- Gettery i settery ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
