package br.com.infnet.hospital_notification_service.notification.controller;

import br.com.infnet.hospital_notification_service.notification.dto.AppointmentNotificationRequest;
import br.com.infnet.hospital_notification_service.notification.dto.NotificationResponseDTO;
import br.com.infnet.hospital_notification_service.notification.model.Notification;
import br.com.infnet.hospital_notification_service.notification.model.NotificationStatus;
import br.com.infnet.hospital_notification_service.notification.model.NotificationType;
import br.com.infnet.hospital_notification_service.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponseDTO create(@RequestBody AppointmentNotificationRequest request) {

        return notificationService.create(request);
    }

    @GetMapping
    public List<NotificationResponseDTO> findAll() {

        return notificationService.findAll();
    }

    @GetMapping("/{id}")
    public NotificationResponseDTO findById(@PathVariable Long id) {
        return notificationService.findById(id);
    }

    @GetMapping("/types/{type}")
    public List<NotificationResponseDTO> findByType(@PathVariable NotificationType type) {
        return notificationService.findByType(type);
    }

    @GetMapping("/status/{status}")
    public List<NotificationResponseDTO> findByStatus(@PathVariable NotificationStatus status) {
        return notificationService.findByStatus(status);
    }

    @PostMapping("/{id}/retry")
    public NotificationResponseDTO retry(@PathVariable Long id) {
        return notificationService.retry(id);
    }

    @PatchMapping("/{id}/cancel")
    public NotificationResponseDTO cancel(@PathVariable Long id) {
        return notificationService.cancel(id);
    }

}