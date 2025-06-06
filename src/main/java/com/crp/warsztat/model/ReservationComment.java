package com.crp.warsztat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Komentarz do rezerwacji — klient i warsztat mogą wymieniać wiadomości w ramach jednej rezerwacji.
 */
@Entity
public class ReservationComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unikalny numer komentarza

    @ManyToOne
    private Reservation reservation; // Do której rezerwacji należy komentarz

    @Enumerated(EnumType.STRING)
    private ReservationCommentAuthorType authorType; // CLIENT lub ADMIN

    private String authorName;   // Imię klienta lub pracownika (opcjonalnie)
    private String content;      // Treść komentarza
    private LocalDateTime createdAt; // Data/godzina utworzenia komentarza

    public ReservationComment() {}

    public ReservationComment(Reservation reservation, ReservationCommentAuthorType authorType, String authorName, String content, LocalDateTime createdAt) {
        this.reservation = reservation;
        this.authorType = authorType;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Gettery i settery...

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public ReservationCommentAuthorType getAuthorType() {
        return authorType;
    }

    public void setAuthorType(ReservationCommentAuthorType authorType) {
        this.authorType = authorType;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
