package com.crp.warsztat.service;

import com.crp.warsztat.model.Reservation;
import com.crp.warsztat.model.ReservationStatus;
import com.crp.warsztat.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testy silnika planowania: przenoszenie końca wizyty poza godziny pracy
 * (pon-pt 08:00-16:00) oraz przydział jednego z 6 stanowisk.
 */
class SchedulingServiceTest {

    private ReservationRepository reservationRepository;
    private SchedulingService schedulingService;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        schedulingService = new SchedulingService(reservationRepository);
    }

    @Test
    void mieszczacaSieWJednymDniu_koncsyTegoSamegoDnia() {
        // Poniedziałek 8.06.2026, start 09:00, usługa 60 minut
        var span = schedulingService.computeSpan(LocalDate.of(2026, 6, 8), LocalTime.of(9, 0), 60);

        assertThat(span.endDate()).isEqualTo(LocalDate.of(2026, 6, 8));
        assertThat(span.endTime()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    void przekraczajacaGodzinyDnia_przenosiSieNaNastepnyDzienRoboczy() {
        // Poniedziałek, start 14:00, usługa 180 minut (3h) -> zostaje 2h do 16:00, brakuje 1h -> wtorek 09:00
        var span = schedulingService.computeSpan(LocalDate.of(2026, 6, 8), LocalTime.of(14, 0), 180);

        assertThat(span.endDate()).isEqualTo(LocalDate.of(2026, 6, 9));
        assertThat(span.endTime()).isEqualTo(LocalTime.of(9, 0));
    }

    @Test
    void zPiatkuNaPoniedzialek_pomijaWeekend() {
        // Piątek 12.06.2026, start 15:00, usługa 120 minut -> 1h do 16:00, brakuje 1h -> pomija sob/niedz -> poniedziałek 09:00
        var span = schedulingService.computeSpan(LocalDate.of(2026, 6, 12), LocalTime.of(15, 0), 120);

        assertThat(span.endDate()).isEqualTo(LocalDate.of(2026, 6, 15)); // poniedziałek
        assertThat(span.endTime()).isEqualTo(LocalTime.of(9, 0));
    }

    @Test
    void wieluDniowaUsluga_liczyPelneDniRobocze() {
        // Poniedziałek 8.06.2026, start 08:00, usługa 2880 minut = 48h = 6 pełnych dni roboczych po 8h
        var span = schedulingService.computeSpan(LocalDate.of(2026, 6, 8), LocalTime.of(8, 0), 2880);

        // 6 dni roboczych od poniedziałku: pn,wt,śr,czw,pt (5 dni pełnych = 40h), potem kolejny poniedziałek 8h -> kończy się 16:00
        assertThat(span.endDate()).isEqualTo(LocalDate.of(2026, 6, 15));
        assertThat(span.endTime()).isEqualTo(LocalTime.of(16, 0));
    }

    @Test
    void startWWeekend_rzucaWyjatek() {
        assertThatThrownBy(() ->
                schedulingService.computeSpan(LocalDate.of(2026, 6, 13), LocalTime.of(9, 0), 60) // sobota
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void startPozaGodzinamiPracy_rzucaWyjatek() {
        assertThatThrownBy(() ->
                schedulingService.computeSpan(LocalDate.of(2026, 6, 8), LocalTime.of(7, 0), 60)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void znajdujeWolneStanowisko_gdyWszystkieZajete_rzucaWyjatek() {
        LocalDate day = LocalDate.of(2026, 6, 8);
        var span = schedulingService.computeSpan(day, LocalTime.of(9, 0), 60);

        List<Reservation> zajete = List.of(
                reservationNaStanowisku(1, day, "09:00", "10:00"),
                reservationNaStanowisku(2, day, "09:00", "10:00"),
                reservationNaStanowisku(3, day, "09:00", "10:00"),
                reservationNaStanowisku(4, day, "09:00", "10:00"),
                reservationNaStanowisku(5, day, "09:00", "10:00"),
                reservationNaStanowisku(6, day, "09:00", "10:00")
        );
        when(reservationRepository.findAll()).thenReturn(zajete);

        assertThatThrownBy(() -> schedulingService.findFreeStation(span, null))
                .isInstanceOf(NoAvailableStationException.class);
    }

    @Test
    void znajdujeWolneStanowisko_gdyJednoWolne_przydzielaJe() {
        LocalDate day = LocalDate.of(2026, 6, 8);
        var span = schedulingService.computeSpan(day, LocalTime.of(9, 0), 60);

        List<Reservation> zajete = List.of(
                reservationNaStanowisku(1, day, "09:00", "10:00"),
                reservationNaStanowisku(2, day, "09:00", "10:00")
        );
        when(reservationRepository.findAll()).thenReturn(zajete);

        int station = schedulingService.findFreeStation(span, null);
        assertThat(station).isEqualTo(3);
    }

    private Reservation reservationNaStanowisku(int station, LocalDate day, String start, String end) {
        Reservation r = new Reservation();
        r.setId((long) station);
        r.setStatus(ReservationStatus.ACCEPTED);
        r.setStationNumber(station);
        r.setVisitDate(day.toString());
        r.setVisitTime(start);
        r.setEndDate(day.toString());
        r.setEndTime(end);
        return r;
    }
}
