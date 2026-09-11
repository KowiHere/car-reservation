package com.crp.warsztat.controller;

import com.crp.warsztat.model.Reservation;
import com.crp.warsztat.model.ReservationStatus;
import com.crp.warsztat.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testy kontrolera rezerwacji — bez prawdziwej bazy danych (repozytorium jest mockiem)
 * i bez filtrów bezpieczeństwa (testujemy tylko logikę kontrolera, nie autoryzację).
 */
@WebMvcTest(ReservationApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationRepository reservationRepository;

    private Reservation sampleReservation() {
        Reservation r = new Reservation();
        r.setId(1L);
        r.setFirstName("Jan");
        r.setLastName("Kowalski");
        r.setEmail("jan@example.com");
        r.setPhoneNumber("123456789");
        r.setVisitDate("2025-06-08");
        r.setVisitTime("08:30");
        r.setStatus(ReservationStatus.PENDING);
        return r;
    }

    @Test
    void createReservation_ustawiaStatusPendingNiezaleznieOdInputu() throws Exception {
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        String body = """
                {
                  "firstName": "Jan",
                  "lastName": "Kowalski",
                  "email": "jan@example.com",
                  "phoneNumber": "123456789",
                  "visitDate": "2025-06-08",
                  "visitTime": "08:30",
                  "status": "ACCEPTED"
                }
                """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createReservation_bezWymaganegoEmaila_zwracaBadRequest() throws Exception {
        String body = """
                {
                  "firstName": "Jan",
                  "lastName": "Kowalski",
                  "phoneNumber": "123456789",
                  "visitDate": "2025-06-08",
                  "visitTime": "08:30"
                }
                """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReservationById_gdyNieIstnieje_zwraca404() throws Exception {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReservationById_gdyIstnieje_zwracaRezerwacje() throws Exception {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(sampleReservation()));

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jan"));
    }

    @Test
    void deleteReservation_gdyNieIstnieje_zwraca404() throws Exception {
        when(reservationRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/reservations/99"))
                .andExpect(status().isNotFound());

        verify(reservationRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateStatus_zNiepoprawnaWartoscia_zwracaBadRequest() throws Exception {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(sampleReservation()));

        mockMvc.perform(patch("/api/reservations/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"NIEISTNIEJACY_STATUS\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_zPoprawnaWartoscia_zwraca200() throws Exception {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(sampleReservation()));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(patch("/api/reservations/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"ACCEPTED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }
}
