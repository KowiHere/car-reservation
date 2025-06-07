package com.crp.warsztat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Klasa Client reprezentuje jednego klienta warsztatu.
 * Każdy klient ma swój unikalny numer (id), dane kontaktowe
 * oraz notatki widoczne tylko dla pracowników.
 */
@Entity // Zamienia klasę na tabelę w bazie danych
public class Client {

    @Id // Klucz główny tabeli (unikalny numer klienta)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Numer nadawany automatycznie przez bazę
    private Long id;

    private String name;    // Imię i nazwisko lub nazwa firmy
    private String email;   // Adres e-mail (opcjonalny, ale wymagany kontakt)
    private String phone;   // Numer telefonu (jw.)
    private boolean wantsNotifications; // Czy klient chce powiadomienia e-mail/SMS

    private String internalNotes; // Notatki widoczne tylko dla pracowników

    // --- Konstruktory (potrzebne dla JPA i dla Twojej wygody) ---

    public Client() {
        // Bezparametrowy konstruktor wymagany przez JPA
    }

    // --- Gettery i settery (potrzebne do odczytu/zapisu pól) ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isWantsNotifications() {
        return wantsNotifications;
    }

    public void setWantsNotifications(boolean wantsNotifications) {
        this.wantsNotifications = wantsNotifications;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public void setFirstName(String s) {
    }
}
