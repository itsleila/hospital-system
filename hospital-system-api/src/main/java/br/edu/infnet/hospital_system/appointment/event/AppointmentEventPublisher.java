package br.edu.infnet.hospital_system.appointment.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import br.edu.infnet.hospital_system.config.RabbitMQConfig;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AppointmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AppointmentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(AppointmentEvent event) {
        String routingKey = switch (event.eventType()) {

                    case "APPOINTMENT_CREATED" -> RabbitMQConfig.CREATED_ROUTING_KEY;
                    case "APPOINTMENT_UPDATED" -> RabbitMQConfig.UPDATED_ROUTING_KEY;
                    case "APPOINTMENT_CANCELLED" -> RabbitMQConfig.CANCELLED_ROUTING_KEY;

                    default -> throw new IllegalArgumentException("Unknown event type: " + event.eventType());
                };

        rabbitTemplate.convertAndSend(RabbitMQConfig.APPOINTMENT_EXCHANGE, routingKey, event);
    }
}