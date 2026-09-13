package com.crp.warsztat.controller;

import com.crp.warsztat.model.Client;
import com.crp.warsztat.repository.ClientRepository;
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

@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientRepository clientRepository;

    @Test
    void getClientById_gdyIstnieje_zwracaKlienta() throws Exception {
        Client client = new Client();
        client.setName("Jan Kowalski");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        mockMvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jan Kowalski"));
    }

    @Test
    void getClientById_gdyNieIstnieje_zwraca404() throws Exception {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createClient_zapisujeIZwracaKlienta() throws Exception {
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Anna Nowak\", \"email\": \"anna@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Anna Nowak"));
    }

    @Test
    void deleteClient_gdyNieIstnieje_zwraca404INieWywolujeDeleteById() throws Exception {
        when(clientRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/clients/99"))
                .andExpect(status().isNotFound());

        verify(clientRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteClient_gdyIstnieje_usuwa() throws Exception {
        when(clientRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/clients/1"))
                .andExpect(status().isOk());

        verify(clientRepository).deleteById(1L);
    }
}
