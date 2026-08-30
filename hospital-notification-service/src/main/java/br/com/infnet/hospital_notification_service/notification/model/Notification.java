package br.com.infnet.hospital_notification_service.notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long appointmentId;
    @Column(nullable = false)
    private Long patientId;
    @Column(nullable = false)
    private String patientName;
    @Column(nullable = false)
    private String patientPhone;

    @Column(nullable = false)
    private String doctorName;
    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;
    @Column(nullable = true)
    private String appointmentStatus;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;
}
