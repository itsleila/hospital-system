package br.edu.infnet.hospital_system.appointment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.edu.infnet.hospital_system.appointment.model.AppointmentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.edu.infnet.hospital_system.appointment.dto.AppointmentRequestDTO;
import br.edu.infnet.hospital_system.appointment.dto.AppointmentResponseDTO;
import br.edu.infnet.hospital_system.appointment.model.Appointment;
import br.edu.infnet.hospital_system.doctor.service.DoctorService;
import br.edu.infnet.hospital_system.patient.service.PatientService;

@Service
public class AppointmentService {
  private static final List<Appointment> appointmentList = new ArrayList<>();
  private Long idCounter = 1L;
  private final PatientService patientService;
  private final DoctorService doctorService;

  public AppointmentService(PatientService patientService, DoctorService doctorService) {
    this.patientService = patientService;
    this.doctorService = doctorService;
  }

  public Appointment toEntity(AppointmentRequestDTO appointmentRequestDTO) {
    Appointment appointment = new Appointment();
    appointment.setId(idCounter++);
    appointment.setPatient(patientService.verifyPatientById(appointmentRequestDTO.getPatientId()));
    appointment.setDoctor(doctorService.verifyDoctorById(appointmentRequestDTO.getDoctorId()));
    appointment.setStatus(AppointmentStatus.SCHEDULED);
    appointment.setDateTime(appointmentRequestDTO.getDateTime());
    return appointment;
  }

  public AppointmentResponseDTO toDTO(Appointment appointment) {
    AppointmentResponseDTO dto = new AppointmentResponseDTO();
    dto.setId(appointment.getId());
    dto.setPatientName(appointment.getPatient().getName() + " " + appointment.getPatient().getSurname());
    dto.setDoctorName(appointment.getDoctor().getName() + " " + appointment.getDoctor().getSurname());
    dto.setStatus(appointment.getStatus());
    dto.setDateTime(appointment.getDateTime());
    return dto;
  }

  public Appointment verifyAppointmentById(Long id) {
    return appointmentList.stream().filter(appointment -> appointment.getId().equals(id)).findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
  }

  public Boolean verifyDoctorAvailability(Long doctorId, LocalDateTime dateTime) {
    return appointmentList.stream().anyMatch(
        appointment -> appointment.getDoctor().getId().equals(doctorId) && appointment.getDateTime().equals(dateTime));
  }

  public AppointmentResponseDTO createAppointment(AppointmentRequestDTO appointmentRequest) {
    Boolean doctorUnavailable = verifyDoctorAvailability(appointmentRequest.getDoctorId(),
        appointmentRequest.getDateTime());
    if (doctorUnavailable) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor is not available at the requested time");
    }
    Appointment appointment = toEntity(appointmentRequest);
    appointmentList.add(appointment);
    return toDTO(appointment);
  }

  public void deleteAppointment(Long id) {
    Appointment appointment = verifyAppointmentById(id);
    appointmentList.remove(appointment);
  }

  public AppointmentResponseDTO getAppointmentById(Long id) {
    Appointment appointment = verifyAppointmentById(id);
    return toDTO(appointment);
  }

  public List<AppointmentResponseDTO> getAllAppointments() {
    return appointmentList.stream().map(this::toDTO).collect(Collectors.toList());
  }

  public List<AppointmentResponseDTO> getAppointmentsByPatientId(Long patientId) {
    return appointmentList.stream().filter(appointment -> appointment.getPatient().getId().equals(patientId))
        .map(this::toDTO).collect(Collectors.toList());
  }

  public List<AppointmentResponseDTO> getAppointmentsByPatientCPF(String patientCPF) {
    return appointmentList.stream().filter(appointment -> appointment.getPatient().getCpf().equals(patientCPF))
        .map(this::toDTO).collect(Collectors.toList());
  }

  public List<AppointmentResponseDTO> getAppointmentsByDoctorId(Long doctorId) {
    return appointmentList.stream().filter(appointment -> appointment.getDoctor().getId().equals(doctorId))
        .map(this::toDTO).collect(Collectors.toList());
  }

  public List<AppointmentResponseDTO> getAppointmentsByDate(LocalDateTime dateTime) {
    return appointmentList.stream()
        .filter(appointment -> appointment.getDateTime().toLocalDate().equals(dateTime.toLocalDate())).map(this::toDTO)
        .collect(Collectors.toList());
  }

  public AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO appointmentRequest) {
    Appointment existingAppointment = verifyAppointmentById(id);
    Boolean doctorUnavailable = verifyDoctorAvailability(appointmentRequest.getDoctorId(),
        appointmentRequest.getDateTime());
    if (doctorUnavailable && !existingAppointment.getDoctor().getId().equals(appointmentRequest.getDoctorId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor is not available at the requested time");
    }
    existingAppointment.setDoctor(doctorService.verifyDoctorById(appointmentRequest.getDoctorId()));
    existingAppointment.setDateTime(appointmentRequest.getDateTime());
    return toDTO(existingAppointment);
  }

}
