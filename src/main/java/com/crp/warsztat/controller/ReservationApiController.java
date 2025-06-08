package com.crp.warsztat.controller;

import com.crp.warsztat.dto.ReservationCalendarDTO;
import com.crp.warsztat.model.Reservation;
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
        return reservationRepository.save(reservation);
    }

    // DELETE /api/reservations/{id}
    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationRepository.deleteById(id);
    }
    // --- --- --- --- --- ---
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
