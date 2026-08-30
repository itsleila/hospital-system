package br.com.infnet.hospital_notification_service.notification.service;

import br.com.infnet.hospital_notification_service.notification.dto.AppointmentNotificationRequest;
import br.com.infnet.hospital_notification_service.notification.dto.NotificationResponseDTO;
import br.com.infnet.hospital_notification_service.notification.model.Notification;
import br.com.infnet.hospital_notification_service.notification.model.NotificationStatus;
import br.com.infnet.hospital_notification_service.notification.model.NotificationType;
import br.com.infnet.hospital_notification_service.notification.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    private Notification toEntity(AppointmentNotificationRequest request) {
        Notification notification = new Notification();

        notification.setAppointmentId(request.appointmentId());
        notification.setPatientId(request.patientId());
        notification.setPatientName(request.patientName());
        notification.setPatientPhone(request.patientPhone());

        notification.setDoctorName(request.doctorName());
        notification.setAppointmentDateTime(request.appointmentDateTime());
        notification.setAppointmentStatus(request.appointmentStatus());
        notification.setType(request.type());
        notification.setStatus(NotificationStatus.PENDING);

        notification.setCreatedAt(LocalDateTime.now());

        return notification;
    }


    private NotificationResponseDTO toDTO(Notification notification) {

        return new NotificationResponseDTO(
                notification.getId(),
                notification.getAppointmentId(),
                notification.getPatientId(),
                notification.getPatientName(),
                notification.getPatientPhone(),
                notification.getDoctorName(),
                notification.getAppointmentDateTime(),
                notification.getAppointmentStatus(),
                notification.getType(),
                notification.getStatus(),
                notification.getCreatedAt(),
                notification.getSentAt()
        );
    }


    private Notification verifyNotificationById(Long id) {
        return notificationRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
    }


    public NotificationResponseDTO create(AppointmentNotificationRequest request) {
        Notification notification = toEntity(request);

        Notification savedNotification = notificationRepository.save(notification);
        return toDTO(savedNotification);
    }


    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> findAll() {
        return notificationRepository
                .findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }


    @Transactional(readOnly = true)
    public NotificationResponseDTO findById(Long id) {
        return toDTO(verifyNotificationById(id));
    }


    @Transactional(readOnly = true)
    public List<NotificationResponseDTO>findByType(NotificationType type) {

        return notificationRepository
                .findByType(type)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> findByStatus(NotificationStatus status) {

        return notificationRepository
                .findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public NotificationResponseDTO retry(Long id) {
        Notification notification = verifyNotificationById(id);

        if (notification.getStatus() != NotificationStatus.FAILED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only failed notifications can be retried");
        }

        notification.setStatus(NotificationStatus.PENDING);
        notification.setSentAt(null);
        Notification updatedNotification = notificationRepository.save(notification);

        return toDTO(updatedNotification);
    }


    public NotificationResponseDTO cancel(Long id) {
        Notification notification = verifyNotificationById(id);

        if (notification.getStatus() == NotificationStatus.SENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sent notifications cannot be cancelled");
        }

        if (notification.getStatus() == NotificationStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Notification is already cancelled");
        }

        notification.setStatus(NotificationStatus.CANCELLED);
        Notification updatedNotification = notificationRepository.save(notification);

        return toDTO(updatedNotification);
    }


}
