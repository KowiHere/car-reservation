package com.crp.warsztat.repository;

import com.crp.warsztat.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repozytorium ReservationRepository
 * Umożliwia operacje CRUD na encji Reservation (rezerwacja).
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Możesz dodać metody, np. szukania rezerwacji po kliencie lub dacie
}
