package com.crp.warsztat.controller;

import com.crp.warsztat.dto.ReservationCalendarDTO;
import com.crp.warsztat.model.Reservation;
import com.crp.warsztat.model.ReservationStatus;
import com.crp.warsztat.repository.ReservationRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationApiController {

    private final ReservationRepository reservationRepository;

    public ReservationApiController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // GET /api/reservations
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // GET /api/reservations/{id}
    @GetMapping("/{id}")
    public Optional<Reservation> getReservationById(@PathVariable Long id) {
        return reservationRepository.findById(id);
    }

    // POST /api/reservations
    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setAdminNotes(""); // Domyślnie puste
        return reservationRepository.save(reservation);
    }


    // DELETE /api/reservations/{id}
    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationRepository.deleteById(id);
    }

    // PUT /api/reservations/{id} – pełna edycja rezerwacji
    @PutMapping("/{id}")
    public Reservation updateReservation(@PathVariable Long id, @RequestBody Reservation updated) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow();

        reservation.setFirstName(updated.getFirstName());
        reservation.setLastName(updated.getLastName());
        reservation.setEmail(updated.getEmail());
        reservation.setPhoneNumber(updated.getPhoneNumber());
        reservation.setCarBrand(updated.getCarBrand());
        reservation.setCarModel(updated.getCarModel());
        reservation.setServiceType(updated.getServiceType());
        reservation.setVisitDate(updated.getVisitDate());
        reservation.setVisitTime(updated.getVisitTime());
        reservation.setClientNotes(updated.getClientNotes());
        reservation.setAdminNotes(updated.getAdminNotes());
        reservation.setStatus(updated.getStatus());

        return reservationRepository.save(reservation);
    }


    // PATCH: /api/reservations/{id}/status – zmiana statusu
    @PatchMapping("/{id}/status")
    public Reservation updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow();
        reservation.setStatus(ReservationStatus.valueOf(body.get("status")));
        return reservationRepository.save(reservation);
    }

    // --- Kalendarz ---

    @GetMapping("/calendar")
    public List<ReservationCalendarDTO> getCalendar() {
        return reservationRepository.findAll().stream()
                .map(res -> new ReservationCalendarDTO(
                        res.getVisitDate(),
                        res.getVisitTime()
                ))
                .toList();
    }

    @GetMapping("/calendar/fullcalendar")
    public List<Map<String, String>> getFullcalendarEvents() {
        return reservationRepository.findAll().stream()
                .filter(res -> res.getStatus() == ReservationStatus.ACCEPTED)
                .map(res -> Map.of(
                        "title", "Zajęte",
                        "start", res.getVisitDate() + "T" + res.getVisitTime(),
                        "end", res.getVisitDate() + "T" + endTime(res.getVisitTime()),
                        "color", "#e95a3e"
                ))
                .toList();
    }

    private String endTime(String startTime) {
        LocalTime start = LocalTime.parse(startTime);
        return start.plusHours(1).toString();
    }

}
