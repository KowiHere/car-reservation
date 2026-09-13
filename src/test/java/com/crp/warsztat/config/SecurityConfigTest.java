package com.crp.warsztat.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sprawdza, że publiczna część aplikacji (formularz klienta, kalendarz, lista usług)
 * działa bez logowania, a wszystko inne (panel admina, edycja/usuwanie danych)
 * wymaga poprawnych danych logowania administratora.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void stronaKlienta_dostepnaBezLogowania() throws Exception {
        mockMvc.perform(get("/index.html")).andExpect(status().isOk());
        mockMvc.perform(get("/styles.css")).andExpect(status().isOk());
    }

    @Test
    void publicznyKalendarz_dostepnyBezLogowania() throws Exception {
        mockMvc.perform(get("/api/reservations/calendar/fullcalendar")).andExpect(status().isOk());
    }

    @Test
    void listaUslug_dostepnaBezLogowania() throws Exception {
        mockMvc.perform(get("/service-types")).andExpect(status().isOk());
    }

    @Test
    void listaWszystkichRezerwacji_bezLogowania_zwraca401() throws Exception {
        mockMvc.perform(get("/api/reservations")).andExpect(status().isUnauthorized());
    }

    @Test
    void listaWszystkichRezerwacji_zPoprawnymLoginemAdmina_zwraca200() throws Exception {
        mockMvc.perform(get("/api/reservations")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("testadmin", "testpass")))
                .andExpect(status().isOk());
    }

    @Test
    void listaWszystkichRezerwacji_zNiepoprawnymHaslem_zwraca401() throws Exception {
        mockMvc.perform(get("/api/reservations")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("testadmin", "zle-haslo")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuwanieRezerwacji_bezLogowania_zwraca401() throws Exception {
        mockMvc.perform(delete("/api/reservations/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void listaKlientow_bezLogowania_zwraca401() throws Exception {
        mockMvc.perform(get("/clients")).andExpect(status().isUnauthorized());
    }
}
