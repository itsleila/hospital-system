package br.edu.infnet.hospital_system.integration.notification.controller;

import br.edu.infnet.hospital_system.integration.notification.NotificationClient;
import br.edu.infnet.hospital_system.integration.notification.dto.NotificationResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationIntegrationController {

    private final NotificationClient notificationClient;

    public NotificationIntegrationController(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @GetMapping
    public List<NotificationResponseDTO> findAll() {
        return notificationClient.findAll();
    }

    @GetMapping("/{id}")
    public NotificationResponseDTO findById(@PathVariable Long id) {
        return notificationClient.findById(id);
    }

    @GetMapping("/types/{types}")
    public List<NotificationResponseDTO> findByType(@PathVariable String type) {
        return notificationClient.findByType(type);
    }

    @GetMapping("/status/{status}")
    public List<NotificationResponseDTO> findByStatus(@PathVariable String status) {
        return notificationClient.findByStatus(status);
    }

    @PostMapping("/{id}/retry")
    public NotificationResponseDTO retry(@PathVariable Long id) {
        return notificationClient.retry(id);
    }

    @PatchMapping("/{id}/cancel")
    public NotificationResponseDTO cancel(@PathVariable Long id) {
        return notificationClient.cancel(id);
    }

}
