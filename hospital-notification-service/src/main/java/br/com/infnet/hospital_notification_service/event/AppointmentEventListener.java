package br.com.infnet.hospital_notification_service.event;

import br.com.infnet.hospital_notification_service.notification.service.NotificationService;
import br.com.infnet.hospital_notification_service.notification.service.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventListener {

    private final NotificationService notificationService;

    public AppointmentEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handle(AppointmentEvent event) {
        notificationService.createFromEvent(event);
    }
}
