package com.crp.warsztat.controller;

import com.crp.warsztat.model.Reservation;
import com.crp.warsztat.repository.ReservationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Kontroler do obsługi rezerwacji (Reservation).
 * Pozwala pobierać, dodawać i usuwać rezerwacje przez REST API.
 */
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Pobierz wszystkie rezerwacje (GET /reservations)
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Pobierz rezerwację po ID (GET /reservations/{id})
    @GetMapping("/{id}")
    public Optional<Reservation> getReservationById(@PathVariable Long id) {
        return reservationRepository.findById(id);
    }

    // Dodaj nową rezerwację (POST /reservations)
    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    // Usuń rezerwację (DELETE /reservations/{id})
    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationRepository.deleteById(id);
    }
}
