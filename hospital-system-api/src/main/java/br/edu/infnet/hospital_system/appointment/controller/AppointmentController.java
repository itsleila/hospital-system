package br.edu.infnet.hospital_system.appointment.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import br.edu.infnet.hospital_system.appointment.dto.AppointmentUpdateRequestDTO;
import br.edu.infnet.hospital_system.appointment.model.RevisionResponseDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.infnet.hospital_system.appointment.dto.AppointmentRequestDTO;
import br.edu.infnet.hospital_system.appointment.dto.AppointmentResponseDTO;
import br.edu.infnet.hospital_system.appointment.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
  private AppointmentService appointmentService;

  public AppointmentController(AppointmentService appointmentService) {
    this.appointmentService = appointmentService;
  }

  @GetMapping
  public ResponseEntity<List<AppointmentResponseDTO>> listAllAppointments() {
    List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
    return ResponseEntity.ok(appointments);
  }

  @PostMapping
  public ResponseEntity<AppointmentResponseDTO> createAppointment(
      @RequestBody AppointmentRequestDTO appointmentRequest) {
    AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(appointmentRequest);
    return ResponseEntity.ok(createdAppointment);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
    appointmentService.deleteAppointment(id);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}/cancel")
  public AppointmentResponseDTO cancelAppointment(@PathVariable Long id) {
      return appointmentService.cancelAppointment(id);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
    AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);
    return ResponseEntity.ok(appointment);
  }

  @GetMapping("/doctor/{doctorId}")
  public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
    List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
    return ResponseEntity.ok(appointments);
  }

  @GetMapping("/patient/{patientId}")
  public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByPatientId(@PathVariable Long patientId) {
    List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPatientId(patientId);
    return ResponseEntity.ok(appointments);
  }

    @GetMapping("/date/{date}")
    public List<AppointmentResponseDTO> findAppointmentByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return appointmentService.getAppointmentsByDate(date);
    }

  @PutMapping("/{id}")
  public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long id, @RequestBody AppointmentUpdateRequestDTO appointmentUpdateDTO) {
    AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(id, appointmentUpdateDTO);
    return ResponseEntity.ok(updatedAppointment);
  }

    @GetMapping("/{id}/history")
    public List<RevisionResponseDTO<AppointmentResponseDTO>>
    getAppointmentHistory(@PathVariable Long id) {
        return appointmentService.getAppointmentHistory(id);
    }

}
