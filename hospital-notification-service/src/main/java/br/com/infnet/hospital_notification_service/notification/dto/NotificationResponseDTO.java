package br.com.infnet.hospital_notification_service.notification.dto;

import br.com.infnet.hospital_notification_service.notification.model.NotificationStatus;
import br.com.infnet.hospital_notification_service.notification.model.NotificationType;

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
        NotificationType type,
        NotificationStatus status,
        LocalDateTime createdAt,
        LocalDateTime sentAt
) {
}
