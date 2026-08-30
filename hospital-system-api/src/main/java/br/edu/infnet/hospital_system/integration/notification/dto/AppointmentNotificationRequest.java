package br.edu.infnet.hospital_system.integration.notification.dto;

import br.edu.infnet.hospital_system.appointment.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentNotificationRequest(
        Long appointmentId,
        Long patientId,
        String patientName,
        String patientPhone,
        String doctorName,
        LocalDateTime appointmentDateTime,
        AppointmentStatus appointmentStatus,
        String type
) {
}