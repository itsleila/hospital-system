package br.edu.infnet.hospital_system.appointment.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.edu.infnet.hospital_system.appointment.dto.AppointmentRequestDTO;
import br.edu.infnet.hospital_system.appointment.dto.AppointmentResponseDTO;
import br.edu.infnet.hospital_system.appointment.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "http://localhost:5173")
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

  @GetMapping("/date")
  public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByDate(@RequestParam LocalDateTime date) {
    List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByDate(date);
    return ResponseEntity.ok(appointments);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long id,
      @RequestBody AppointmentRequestDTO appointmentRequest) {
    AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(id, appointmentRequest);
    return ResponseEntity.ok(updatedAppointment);
  }

}
