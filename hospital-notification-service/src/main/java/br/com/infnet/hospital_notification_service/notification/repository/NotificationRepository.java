package br.com.infnet.hospital_notification_service.notification.repository;

import br.com.infnet.hospital_notification_service.notification.model.Notification;
import br.com.infnet.hospital_notification_service.notification.model.NotificationStatus;
import br.com.infnet.hospital_notification_service.notification.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByAppointmentId(Long appointmentId);
    List<Notification> findByType(NotificationType type);
    List<Notification> findByStatus(NotificationStatus status);
}