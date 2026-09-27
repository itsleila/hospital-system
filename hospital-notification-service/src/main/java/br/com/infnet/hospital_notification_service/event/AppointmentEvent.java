package br.com.infnet.hospital_notification_service.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentEvent(

        UUID eventId,
        Long appointmentId,
        Long patientId,
        String patientName,
        String patientPhone,
        String doctorName,
        LocalDateTime appointmentDateTime,
        String eventType,
        Instant occurredAt

) {
}