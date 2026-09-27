package br.com.infnet.hospital_notification_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.infnet.hospital_notification_service.notification.dto.NotificationResponseDTO;
import br.com.infnet.hospital_notification_service.notification.model.Notification;
import br.com.infnet.hospital_notification_service.notification.model.NotificationStatus;
import br.com.infnet.hospital_notification_service.notification.model.NotificationType;
import br.com.infnet.hospital_notification_service.event.AppointmentEvent;
import br.com.infnet.hospital_notification_service.notification.repository.NotificationRepository;

import br.com.infnet.hospital_notification_service.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import org.junit.jupiter.api.DisplayName;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Deve criar uma notificação a partir de um evento de agendamento")
    void deveCriarUmaNotificacao() {

        AppointmentEvent event = createEvent("APPOINTMENT_CREATED");

        when(notificationRepository.existsByEventId(event.eventId())).thenReturn(false);

        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> {
                    Notification notification = invocation.getArgument(0);
                    ReflectionTestUtils.setField(notification, "id", 1L);
                    return notification;
                });

        NotificationResponseDTO response = notificationService.createFromEvent(event);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.appointmentId()).isEqualTo(10L);
        assertThat(response.patientId()).isEqualTo(5L);
        assertThat(response.patientName()).isEqualTo("Maria Silva");
        assertThat(response.doctorName()).isEqualTo("João Souza");

        assertThat(response.type()).isEqualTo(NotificationType.APPOINTMENT_CREATED);
        assertThat(response.status()).isEqualTo(NotificationStatus.PENDING);

        assertThat(response.createdAt()).isNotNull();
        assertThat(response.sentAt()).isNull();

        verify(notificationRepository).existsByEventId(event.eventId());

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("Deve salvar os dados corretos da notificação a partir do evento")
    void deveSalvarOsDadosCorretamente() {

        AppointmentEvent event = createEvent("APPOINTMENT_UPDATED");

        when(notificationRepository.existsByEventId(event.eventId())).thenReturn(false);

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.createFromEvent(event);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();

        assertThat(saved.getEventId()).isEqualTo(event.eventId());
        assertThat(saved.getAppointmentId()).isEqualTo(10L);
        assertThat(saved.getPatientId()).isEqualTo(5L);
        assertThat(saved.getPatientName()).isEqualTo("Maria Silva");
        assertThat(saved.getPatientPhone()).isEqualTo("11999999999");
        assertThat(saved.getDoctorName()).isEqualTo("João Souza");

        assertThat(saved.getAppointmentDateTime()).isEqualTo(LocalDateTime.of(2026, 10, 20, 14, 0));

        assertThat(saved.getType()).isEqualTo(NotificationType.APPOINTMENT_UPDATED);

        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.PENDING);

        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve encontrar uma notificação por ID")
    void deveEncontrarUmaNotificacaoPeloID() {

        Notification notification = createNotification(NotificationStatus.PENDING);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        NotificationResponseDTO response = notificationService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.patientName()).isEqualTo("Maria Silva");

        assertThat(response.status()).isEqualTo(NotificationStatus.PENDING);

        verify(notificationRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve encontrar notificações por status")
    void deveEncontrarNotificacoesPorStatus() {

        Notification notification = createNotification(NotificationStatus.FAILED);
        when(notificationRepository.findByStatus(NotificationStatus.FAILED)).thenReturn(List.of(notification));

        List<NotificationResponseDTO> result = notificationService.findByStatus(NotificationStatus.FAILED);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().status()).isEqualTo(NotificationStatus.FAILED);

        verify(notificationRepository).findByStatus(NotificationStatus.FAILED);
    }

    @Test
    @DisplayName("Deve cancelar uma notificação pendente")
    void deveCancelarUmaNotificacao() {

        Notification notification = createNotification(NotificationStatus.PENDING);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);

        NotificationResponseDTO response = notificationService.cancel(1L);

        assertThat(response.status()).isEqualTo(NotificationStatus.CANCELLED);
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Deve rejeitar o cancelamento quando a notificação já foi enviada")
    void naoDeveCancelarUmaNotificacaoJaEnviada() {
        Notification notification = createNotification(NotificationStatus.SENT);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.cancel(1L)).isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                });

        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar o cancelamento quando a notificação já está cancelada")
    void naoDeveCancelarUmaNotificacaoJaCancelada() {

        Notification notification = createNotification(
                NotificationStatus.CANCELLED);

        when(notificationRepository.findById(1L))
                .thenReturn(
                        Optional.of(notification));

        assertThatThrownBy(() -> notificationService.cancel(1L))
                .isInstanceOf(
                        ResponseStatusException.class)
                .satisfies(exception -> {

                    ResponseStatusException responseException = (ResponseStatusException) exception;

                    assertThat(
                            responseException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                });

        verify(
                notificationRepository,
                never()).save(any());
    }

    private AppointmentEvent createEvent(String eventType) {

        return new AppointmentEvent(
                UUID.randomUUID(),
                10L,
                5L,
                "Maria Silva",
                "11999999999",
                "João Souza",

                LocalDateTime.of(2026, 10, 20, 14, 0),
                eventType,
                Instant.parse("2026-09-27T15:00:00Z"));
    }

    private Notification createNotification(NotificationStatus status) {

        Notification notification = new Notification();

        ReflectionTestUtils.setField(notification, "id", 1L);
        notification.setEventId(UUID.randomUUID());

        notification.setAppointmentId(10L);
        notification.setPatientId(5L);

        notification.setPatientName("Maria Silva");
        notification.setPatientPhone("11999999999");
        notification.setDoctorName("João Souza");

        notification.setAppointmentDateTime(LocalDateTime.of(2026, 10, 20, 14, 0));
        notification.setType(NotificationType.APPOINTMENT_CREATED);

        notification.setStatus(status);

        notification.setCreatedAt(LocalDateTime.of(2026, 9, 27, 12, 0));

        return notification;
    }
}
