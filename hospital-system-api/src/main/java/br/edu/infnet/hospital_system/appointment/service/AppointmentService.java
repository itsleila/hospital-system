package br.edu.infnet.hospital_system.appointment.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import br.edu.infnet.hospital_system.appointment.dto.AppointmentRequestDTO;
import br.edu.infnet.hospital_system.appointment.dto.AppointmentResponseDTO;
import br.edu.infnet.hospital_system.appointment.dto.AppointmentUpdateRequestDTO;
import br.edu.infnet.hospital_system.appointment.model.Appointment;
import br.edu.infnet.hospital_system.appointment.model.AppointmentStatus;
import br.edu.infnet.hospital_system.appointment.model.RevisionResponseDTO;
import br.edu.infnet.hospital_system.appointment.repository.AppointmentRepository;
import br.edu.infnet.hospital_system.doctor.service.DoctorService;
import br.edu.infnet.hospital_system.integration.notification.NotificationClient;
import br.edu.infnet.hospital_system.integration.notification.dto.AppointmentNotificationRequest;
import br.edu.infnet.hospital_system.patient.service.PatientService;
import org.springframework.data.history.Revision;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final NotificationClient notificationClient;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientService patientService, DoctorService doctorService, NotificationClient notificationClient) {

        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.notificationClient = notificationClient;
    }

    private Appointment toEntity(AppointmentRequestDTO appointmentRequestDTO) {

        Appointment appointment = new Appointment();

        appointment.setPatient(patientService.verifyPatientById(appointmentRequestDTO.getPatientId()));
        appointment.setDoctor(doctorService.verifyDoctorById(appointmentRequestDTO.getDoctorId()));

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setDateTime(appointmentRequestDTO.getDateTime());

        return appointment;
    }

    private AppointmentResponseDTO toDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();

        dto.setId(appointment.getId());
        dto.setPatientName(appointment.getPatient().getName() + " " + appointment.getPatient().getSurname());
        dto.setDoctorName(appointment.getDoctor().getName() + " " + appointment.getDoctor().getSurname());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setStatus(appointment.getStatus());
        dto.setDateTime(appointment.getDateTime());

        return dto;
    }

    private AppointmentNotificationRequest createNotificationRequest(Appointment appointment, String type) {
        return new AppointmentNotificationRequest(
            appointment.getId(),
            appointment.getPatient().getId(),
            appointment.getPatient().getName() + " " + appointment.getPatient().getSurname(),
            appointment.getPatient().getPhonenumber(),
            appointment.getDoctor().getName() + " " + appointment.getDoctor().getSurname(),
            appointment.getDateTime(),
            appointment.getStatus(),
            type
        );
    }

    @Transactional(readOnly = true)
    public Appointment verifyAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
    }

    @Transactional(readOnly = true)
    public boolean verifyDoctorAvailability(Long doctorId, LocalDateTime dateTime) {
        return appointmentRepository.existsByDoctor_IdAndDateTime(doctorId, dateTime);
    }

    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO appointmentRequest) {

        boolean doctorUnavailable = appointmentRepository.existsByDoctor_IdAndDateTime(appointmentRequest.getDoctorId(), appointmentRequest.getDateTime());

        if (doctorUnavailable) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Doctor is not available at the requested time");
        }

        Appointment appointment = toEntity(appointmentRequest);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        notificationClient.createNotification(createNotificationRequest(savedAppointment, "APPOINTMENT_CREATED"));

        return toDTO(savedAppointment);
    }

    public void deleteAppointment(Long id) {
        Appointment appointment = verifyAppointmentById(id);
        appointmentRepository.delete(appointment);
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO getAppointmentById(Long id) {
        return toDTO(verifyAppointmentById(id));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsByPatientId(Long patientId) {

        return appointmentRepository.findByPatient_Id(patientId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsByPatientCPF(String patientCPF) {

        return appointmentRepository.findByPatient_Cpf(patientCPF)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctor_Id(doctorId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getAppointmentsByDate(LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return appointmentRepository
                .findByDateTimeGreaterThanEqualAndDateTimeLessThan(start, end)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public AppointmentResponseDTO updateAppointment(Long id, AppointmentUpdateRequestDTO appointmentUpdateDTO) {
        Appointment existingAppointment = verifyAppointmentById(id);
        boolean doctorUnavailable = appointmentRepository.existsByDoctor_IdAndDateTimeAndIdNot(appointmentUpdateDTO.getDoctorId(), appointmentUpdateDTO.getDateTime(), id);

        if (doctorUnavailable) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Doctor is not available at the requested time");
        }

        existingAppointment.setDoctor(doctorService.verifyDoctorById(appointmentUpdateDTO.getDoctorId()));
        existingAppointment.setDateTime(appointmentUpdateDTO.getDateTime());
        existingAppointment.setStatus(appointmentUpdateDTO.getStatus());

        Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
        notificationClient.createNotification(createNotificationRequest(updatedAppointment, "APPOINTMENT_UPDATED"));


        return toDTO(updatedAppointment);
    }

    public AppointmentResponseDTO cancelAppointment(Long id) {
        Appointment appointment = verifyAppointmentById(id);

        appointment.setStatus(AppointmentStatus.CANCELED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);

        notificationClient.createNotification(createNotificationRequest(cancelledAppointment, "APPOINTMENT_CANCELLED"));
        return toDTO(cancelledAppointment);
    }

    private RevisionResponseDTO<AppointmentResponseDTO> toHistoryDTO(Revision<Integer, Appointment> revision) {
        return new RevisionResponseDTO<>(
                revision.getRequiredRevisionNumber(),
                revision.getMetadata().getRevisionInstant().orElse(null),
                revision.getMetadata().getRevisionType().name(),
                toDTO(revision.getEntity())
        );
    }

    @Transactional(readOnly = true)
    public List<RevisionResponseDTO<AppointmentResponseDTO>>
    getAppointmentHistory(Long id) {
        return appointmentRepository.findRevisions(id)
                .stream()
                .map(this::toHistoryDTO)
                .toList();
    }
}