package com.crp.warsztat.controller;

import com.crp.warsztat.model.ServiceType;
import com.crp.warsztat.repository.ServiceTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceTypeRepository serviceTypeRepository;

    @Test
    void getAllServiceTypes_zwracaListe() throws Exception {
        ServiceType przeglad = new ServiceType("Przegląd", "Przegląd okresowy", 60, "Przeglądy");
        when(serviceTypeRepository.findAll()).thenReturn(List.of(przeglad));

        mockMvc.perform(get("/service-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Przegląd"))
                .andExpect(jsonPath("$[0].durationMinutes").value(60));
    }

    @Test
    void getServiceTypeById_gdyNieIstnieje_zwraca404() throws Exception {
        when(serviceTypeRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/service-types/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createServiceType_zapisuje() throws Exception {
        when(serviceTypeRepository.save(any(ServiceType.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Wymiana opon\", \"durationMinutes\": 45, \"category\": \"Przeglądy\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wymiana opon"));
    }

    @Test
    void deleteServiceType_gdyNieIstnieje_zwraca404() throws Exception {
        when(serviceTypeRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/service-types/99"))
                .andExpect(status().isNotFound());

        verify(serviceTypeRepository, never()).deleteById(anyLong());
    }
}
