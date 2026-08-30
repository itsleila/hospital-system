package br.edu.infnet.hospital_system.integration.notification;

import br.edu.infnet.hospital_system.integration.notification.dto.AppointmentNotificationRequest;
import br.edu.infnet.hospital_system.integration.notification.dto.NotificationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "hospital-notification-service")
public interface NotificationClient {

    @PostMapping("/notifications")
    void createNotification(@RequestBody AppointmentNotificationRequest request);

    @GetMapping("/notifications")
    List<NotificationResponseDTO> findAll();

    @GetMapping("/notifications/{id}")
    NotificationResponseDTO findById(@PathVariable Long id);

    @GetMapping("/notifications/types/{types}")
    List<NotificationResponseDTO> findByType(@PathVariable String types);

    @GetMapping("/notifications/status/{status}")
    List<NotificationResponseDTO> findByStatus(@PathVariable String status);

    @PostMapping("/notifications/{id}/retry")
    NotificationResponseDTO retry(@PathVariable Long id);

    @PatchMapping("/notifications/{id}/cancel")
    NotificationResponseDTO cancel(@PathVariable Long id);

}