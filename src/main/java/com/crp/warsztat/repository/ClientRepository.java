package com.crp.warsztat.repository;

import com.crp.warsztat.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repozytorium ClientRepository
 * Umożliwia operacje CRUD na encji Client (klient) bez ręcznego pisania SQL.
 */
public interface ClientRepository extends JpaRepository<Client, Long> {
    // Możesz dodać tu własne metody wyszukiwania, np. po e-mailu:
    // Optional<Client> findByEmail(String email);
}
