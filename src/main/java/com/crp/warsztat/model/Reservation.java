package com.crp.warsztat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Klasa Reservation (Rezerwacja)
 * Reprezentuje pojedynczą rezerwację klienta na wybrane usługi, w wybranym terminie.
 * Zawiera też dane pojazdu oraz notatki.
 */
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Unikalny numer rezerwacji

    // Powiązanie z klientem (wielu rezerwacji może mieć jeden klient)
    @ManyToOne
    private Client client;

    // Lista wybranych usług w ramach tej rezerwacji
    @ManyToMany
    private List<ServiceType> serviceTypes;

    private LocalDateTime startDateTime; // Data i godzina rozpoczęcia
    private LocalDateTime endDateTime;   // Data i godzina zakończenia

    // Status rezerwacji: PENDING, ACCEPTED, REJECTED, CANCELLED
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime createdAt;      // Data utworzenia rezerwacji
    private LocalDateTime lastModifiedAt; // Data ostatniej modyfikacji

    private String clientNotes;   // Notatki klienta
    private String adminNotes;    // Notatki wewnętrzne (dla pracowników/admina)

    // --- Dane pojazdu (opcjonalne, wszystkie mogą być puste) ---
    private String vehicleVin;      // VIN pojazdu
    private String vehicleMake;     // Marka pojazdu
    private String vehicleModel;    // Model pojazdu
    private Integer vehicleYear;    // Rok produkcji
    private String vehicleFuel;     // Typ paliwa
    private Double engineCapacity;  // Pojemność silnika
    private String cabType;         // Typ kabiny

    // --- Konstruktory ---

    public Reservation() {
        // Konstruktor bez parametrów – wymagany przez JPA
    }

    // Możesz dodać własny konstruktor dla wygody (opcjonalnie)

    // --- Gettery i settery ---

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public String getClientNotes() {
        return clientNotes;
    }

    public void setClientNotes(String clientNotes) {
        this.clientNotes = clientNotes;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public String getVehicleVin() {
        return vehicleVin;
    }

    public void setVehicleVin(String vehicleVin) {
        this.vehicleVin = vehicleVin;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public Integer getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(Integer vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public String getVehicleFuel() {
        return vehicleFuel;
    }

    public void setVehicleFuel(String vehicleFuel) {
        this.vehicleFuel = vehicleFuel;
    }

    public Double getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(Double engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public String getCabType() {
        return cabType;
    }

    public void setCabType(String cabType) {
        this.cabType = cabType;
    }
}
