package com.crp.warsztat.controller;

import com.crp.warsztat.model.Client;
import com.crp.warsztat.repository.ClientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Kontroler do obsługi klientów (Client).
 * Pozwala pobierać, dodawać i sprawdzać dane klientów przez REST API.
 */
@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/{id}")
    public Client getClientById(@PathVariable Long id) {
        return clientRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientRepository.save(client);
    }

    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id) {
        if (!clientRepository.existsById(id)) {
            throw new NoSuchElementException("Klient o id " + id + " nie istnieje");
        }
        clientRepository.deleteById(id);
    }
}
