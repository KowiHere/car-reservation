package com.crp.warsztat.service;

/**
 * Rzucany, gdy żadne z 6 stanowisk warsztatu nie jest wolne
 * przez cały wymagany czas trwania usługi.
 */
public class NoAvailableStationException extends RuntimeException {
    public NoAvailableStationException(String message) {
        super(message);
    }
}
