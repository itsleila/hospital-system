package br.edu.infnet.hospital_system.integration.notification.dto;

import java.time.LocalDateTime;

public record NotificationResponseDTO(
        Long id,
        Long appointmentId,
        Long patientId,
        String patientName,
        String patientPhone,
        String doctorName,
        LocalDateTime appointmentDateTime,
        String appointmentStatus,
        String type,
        String status,
        LocalDateTime createdAt,
        LocalDateTime sentAt
) {
}