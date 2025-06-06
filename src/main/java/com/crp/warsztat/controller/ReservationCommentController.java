package com.crp.warsztat.controller;

import com.crp.warsztat.model.ReservationComment;
import com.crp.warsztat.repository.ReservationCommentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Kontroler do obsługi komentarzy do rezerwacji.
 * Pozwala pobierać, dodawać i usuwać komentarze przez REST API.
 */
@RestController
@RequestMapping("/reservation-comments")
public class ReservationCommentController {

    private final ReservationCommentRepository reservationCommentRepository;

    public ReservationCommentController(ReservationCommentRepository reservationCommentRepository) {
        this.reservationCommentRepository = reservationCommentRepository;
    }

    // Pobierz wszystkie komentarze (GET /reservation-comments)
    @GetMapping
    public List<ReservationComment> getAllComments() {
        return reservationCommentRepository.findAll();
    }

    // Pobierz jeden komentarz po ID (GET /reservation-comments/{id})
    @GetMapping("/{id}")
    public Optional<ReservationComment> getCommentById(@PathVariable Long id) {
        return reservationCommentRepository.findById(id);
    }

    // Pobierz wszystkie komentarze dla danej rezerwacji (GET /reservation-comments/by-reservation/{reservationId})
    @GetMapping("/by-reservation/{reservationId}")
    public List<ReservationComment> getCommentsByReservationId(@PathVariable Long reservationId) {
        return reservationCommentRepository.findByReservationId(reservationId);
    }

    // Dodaj komentarz (POST /reservation-comments)
    @PostMapping
    public ReservationComment createComment(@RequestBody ReservationComment comment) {
        return reservationCommentRepository.save(comment);
    }

    // Usuń komentarz (DELETE /reservation-comments/{id})
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        reservationCommentRepository.deleteById(id);
    }
}
