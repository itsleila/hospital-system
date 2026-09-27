package br.edu.infnet.hospital_system.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String APPOINTMENT_EXCHANGE = "hospital.appointments.exchange";

    public static final String CREATED_ROUTING_KEY = "appointment.created";

    public static final String UPDATED_ROUTING_KEY = "appointment.updated";

    public static final String CANCELLED_ROUTING_KEY = "appointment.cancelled";

    @Bean
    public TopicExchange appointmentExchange() {
        return new TopicExchange(APPOINTMENT_EXCHANGE, true, false);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}