package br.edu.infnet.hospital_system.appointment.model;

import java.time.Instant;

public record RevisionResponseDTO<T>(
        Number revision,
        Instant revisionDate,
        String operation,
        T data
) {
}