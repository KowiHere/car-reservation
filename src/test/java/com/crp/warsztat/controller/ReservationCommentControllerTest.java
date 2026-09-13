package com.crp.warsztat.controller;

import com.crp.warsztat.model.ReservationComment;
import com.crp.warsztat.model.ReservationCommentAuthorType;
import com.crp.warsztat.repository.ReservationCommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationCommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationCommentRepository reservationCommentRepository;

    @Test
    void getCommentsByReservationId_zwracaListe() throws Exception {
        ReservationComment comment = new ReservationComment(null, ReservationCommentAuthorType.CLIENT, "Jan", "Pytanie o termin", LocalDateTime.now());
        when(reservationCommentRepository.findByReservationId(5L)).thenReturn(List.of(comment));

        mockMvc.perform(get("/reservation-comments/by-reservation/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Pytanie o termin"));
    }

    @Test
    void getCommentById_gdyNieIstnieje_zwraca404() throws Exception {
        when(reservationCommentRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reservation-comments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment_zapisuje() throws Exception {
        when(reservationCommentRepository.save(any(ReservationComment.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/reservation-comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorType\": \"ADMIN\", \"authorName\": \"Recepcja\", \"content\": \"Termin potwierdzony\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Termin potwierdzony"));
    }

    @Test
    void deleteComment_gdyNieIstnieje_zwraca404() throws Exception {
        when(reservationCommentRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/reservation-comments/99"))
                .andExpect(status().isNotFound());

        verify(reservationCommentRepository, never()).deleteById(anyLong());
    }
}
