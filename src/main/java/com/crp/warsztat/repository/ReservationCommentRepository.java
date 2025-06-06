package com.crp.warsztat.repository;

import com.crp.warsztat.model.ReservationComment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repozytorium ReservationCommentRepository
 * Umożliwia operacje CRUD na komentarzach do rezerwacji.
 */
public interface ReservationCommentRepository extends JpaRepository<ReservationComment, Long> {
    // Tu możesz dodać wyszukiwanie komentarzy do konkretnej rezerwacji
}
