package com.crp.warsztat.controller;

import com.crp.warsztat.dto.ReservationCalendarDTO;
import com.crp.warsztat.model.Reservation;
import com.crp.warsztat.model.ReservationStatus;
import com.crp.warsztat.model.ServiceType;
import com.crp.warsztat.repository.ReservationRepository;
import com.crp.warsztat.repository.ServiceTypeRepository;
import com.crp.warsztat.service.SchedulingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/reservations")
public class ReservationApiController {

    private final ReservationRepository reservationRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final SchedulingService schedulingService;

    public ReservationApiController(ReservationRepository reservationRepository,
                                     ServiceTypeRepository serviceTypeRepository,
                                     SchedulingService schedulingService) {
        this.reservationRepository = reservationRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.schedulingService = schedulingService;
    }

    // GET /api/reservations
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // GET /api/reservations/{id}
    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        return reservationRepository.findById(id).orElseThrow();
    }

    // POST /api/reservations
    @PostMapping
    public Reservation createReservation(@Valid @RequestBody Reservation reservation) {
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setAdminNotes(""); // Domyślnie puste
        scheduleReservation(reservation, null);
        return reservationRepository.save(reservation);
    }


    // DELETE /api/reservations/{id}
    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new NoSuchElementException("Rezerwacja o id " + id + " nie istnieje");
        }
        reservationRepository.deleteById(id);
    }

    // PUT /api/reservations/{id} – pełna edycja rezerwacji
    @PutMapping("/{id}")
    public Reservation updateReservation(@PathVariable Long id, @Valid @RequestBody Reservation updated) {
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
        scheduleReservation(reservation, id);

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
    public List<Map<String, Object>> getFullcalendarEvents() {
        return reservationRepository.findAll().stream()
                .filter(res -> res.getStatus() == ReservationStatus.PENDING || res.getStatus() == ReservationStatus.ACCEPTED)
                .map(res -> Map.<String, Object>of(
                        "title", "Stanowisko " + res.getStationNumber() + " – " + res.getFirstName() + " " + res.getLastName(),
                        "start", res.getVisitDate() + "T" + res.getVisitTime(),
                        "end", res.getEndDate() + "T" + res.getEndTime(),
                        "color", clientColor(res.getEmail())
                ))
                .toList();
    }

    /**
     * Deterministyczny kolor na podstawie e-maila klienta — kolory nie mają znaczenia
     * biznesowego, służą tylko do wizualnego odróżnienia rezerwacji różnych klientów.
     */
    private String clientColor(String email) {
        String[] palette = {"#e95a3e", "#3e8ee9", "#3ee9a0", "#e9c53e", "#a03ee9", "#e93ea0", "#3ee9df"};
        int index = Math.abs((email == null ? "" : email).hashCode()) % palette.length;
        return palette[index];
    }

    /**
     * Wylicza realny koniec wizyty na podstawie czasu trwania wybranej usługi
     * i godzin pracy warsztatu, a następnie przydziela wolne stanowisko.
     */
    private void scheduleReservation(Reservation reservation, Long reservationIdToExclude) {
        if (reservation.getServiceType() == null || reservation.getServiceType().getId() == null) {
            throw new IllegalArgumentException("Wybierz typ usługi");
        }
        ServiceType serviceType = serviceTypeRepository.findById(reservation.getServiceType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Nieznany typ usługi"));
        int durationMinutes = serviceType.getDurationMinutes() != null ? serviceType.getDurationMinutes() : 60;

        LocalDate startDate = LocalDate.parse(reservation.getVisitDate());
        LocalTime startTime = LocalTime.parse(reservation.getVisitTime());
        SchedulingService.Span span = schedulingService.computeSpan(startDate, startTime, durationMinutes);

        reservation.setServiceType(serviceType);
        reservation.setEndDate(span.endDate().toString());
        reservation.setEndTime(span.endTime().toString());
        reservation.setStationNumber(schedulingService.findFreeStation(span, reservationIdToExclude));
    }

}
