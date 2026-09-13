package com.crp.warsztat.service;

import com.crp.warsztat.model.Reservation;
import com.crp.warsztat.model.ReservationStatus;
import com.crp.warsztat.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * Wylicza, kiedy realnie kończy się wizyta (uwzględniając godziny pracy warsztatu
 * i weekendy) oraz przydziela jedno z ograniczonej liczby stanowisk.
 *
 * Warsztat pracuje pon-pt w godzinach 08:00-16:00. Jeśli usługa nie mieści się
 * w pozostałym czasie danego dnia, kontynuuje się ją od 08:00 kolejnego dnia
 * roboczego (piątek -> poniedziałek, z pominięciem soboty i niedzieli).
 */
@Service
public class SchedulingService {

    public static final LocalTime WORK_START = LocalTime.of(8, 0);
    public static final LocalTime WORK_END = LocalTime.of(16, 0);
    public static final int STATION_COUNT = 6;

    private final ReservationRepository reservationRepository;

    public SchedulingService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public record Span(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        public LocalDateTime startDateTime() {
            return LocalDateTime.of(startDate, startTime);
        }

        public LocalDateTime endDateTime() {
            return LocalDateTime.of(endDate, endTime);
        }
    }

    /**
     * Liczy realny termin zakończenia wizyty rozpoczętej o podanej dacie/godzinie,
     * trwającej {@code durationMinutes} minut pracy warsztatu.
     */
    public Span computeSpan(LocalDate startDate, LocalTime startTime, int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Czas trwania usługi musi być większy od zera");
        }
        if (isWeekend(startDate)) {
            throw new IllegalArgumentException("Warsztat nie pracuje w wybranym dniu (sobota/niedziela)");
        }
        if (startTime.isBefore(WORK_START) || !startTime.isBefore(WORK_END)) {
            throw new IllegalArgumentException("Godzina rozpoczęcia poza godzinami pracy warsztatu (08:00-16:00)");
        }

        LocalDate date = startDate;
        LocalTime time = startTime;
        int remainingMinutes = durationMinutes;

        while (remainingMinutes > 0) {
            int minutesLeftToday = (int) Duration.between(time, WORK_END).toMinutes();
            if (remainingMinutes <= minutesLeftToday) {
                time = time.plusMinutes(remainingMinutes);
                remainingMinutes = 0;
            } else {
                remainingMinutes -= minutesLeftToday;
                date = nextWorkingDay(date);
                time = WORK_START;
            }
        }

        return new Span(startDate, startTime, date, time);
    }

    /**
     * Zwraca numer (1-{@value #STATION_COUNT}) pierwszego stanowiska wolnego przez cały czas trwania
     * {@code span}, ignorując rezerwacje odrzucone/anulowane oraz (przy edycji) samą siebie.
     */
    public int findFreeStation(Span span, Long reservationIdToExclude) {
        List<Reservation> active = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() != ReservationStatus.REJECTED && r.getStatus() != ReservationStatus.CANCELLED)
                .filter(r -> reservationIdToExclude == null || !Objects.equals(r.getId(), reservationIdToExclude))
                .filter(r -> r.getStationNumber() != null && r.getEndDate() != null && r.getEndTime() != null)
                .toList();

        for (int station = 1; station <= STATION_COUNT; station++) {
            int currentStation = station;
            boolean overlaps = active.stream()
                    .filter(r -> Objects.equals(r.getStationNumber(), currentStation))
                    .anyMatch(r -> overlaps(span, r));
            if (!overlaps) {
                return station;
            }
        }
        throw new NoAvailableStationException("Brak wolnych stanowisk w wybranym terminie — wszystkie " + STATION_COUNT + " są zajęte");
    }

    private boolean overlaps(Span span, Reservation other) {
        LocalDateTime otherStart = LocalDateTime.of(LocalDate.parse(other.getVisitDate()), LocalTime.parse(other.getVisitTime()));
        LocalDateTime otherEnd = LocalDateTime.of(LocalDate.parse(other.getEndDate()), LocalTime.parse(other.getEndTime()));
        return span.startDateTime().isBefore(otherEnd) && otherStart.isBefore(span.endDateTime());
    }

    private LocalDate nextWorkingDay(LocalDate date) {
        LocalDate next = date.plusDays(1);
        while (isWeekend(next)) {
            next = next.plusDays(1);
        }
        return next;
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
