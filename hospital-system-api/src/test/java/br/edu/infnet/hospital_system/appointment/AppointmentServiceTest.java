package br.edu.infnet.hospital_system.appointment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.edu.infnet.hospital_system.HospitalSystemApplication;
import br.edu.infnet.hospital_system.PostgreSQLIntegrationTest;
import br.edu.infnet.hospital_system.appointment.dto.AppointmentRequestDTO;
import br.edu.infnet.hospital_system.appointment.service.AppointmentService;
import br.edu.infnet.hospital_system.config.TestcontainersConfiguration;
import br.edu.infnet.hospital_system.doctor.dto.DoctorResponseDTO;
import br.edu.infnet.hospital_system.doctor.dto.DoctorResquestDTO;
import br.edu.infnet.hospital_system.doctor.service.DoctorService;
import br.edu.infnet.hospital_system.patient.dto.PatientRequestDTO;
import br.edu.infnet.hospital_system.patient.dto.PatientResponseDTO;
import br.edu.infnet.hospital_system.patient.model.Gender;
import br.edu.infnet.hospital_system.patient.service.PatientService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Transactional
@SpringBootTest(classes = HospitalSystemApplication.class)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AppointmentServiceTest extends PostgreSQLIntegrationTest {
    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public AppointmentServiceTest(AppointmentService appointmentService, PatientService patientService,
            DoctorService doctorService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @Test
    @DisplayName("Deve rejeitar agendamento quando o médico não estiver disponível")
    void shouldRejectAppointmentWhenDoctorIsUnavailable() {
        PatientRequestDTO patientRequest = new PatientRequestDTO();

        patientRequest.setName("Collins");
        patientRequest.setSurname("Bingly");
        patientRequest.setCpf("99900011122");
        patientRequest.setBirthdate(LocalDate.of(1995, 4, 10));
        patientRequest.setGender(Gender.FEMALE);
        patientRequest.setPhonenumber("11955555555");

        PatientResponseDTO patient = patientService.createPatient(patientRequest);
        DoctorResquestDTO doctorRequest = new DoctorResquestDTO();

        doctorRequest.setName("Charlotte");
        doctorRequest.setSurname("Lucas");
        doctorRequest.setEmail("lucasCharlotte@hospital.com");
        doctorRequest.setCRM("CRM-MG-7000");
        doctorRequest.setSpecialty("Neurology");
        DoctorResponseDTO doctor = doctorService.createDoctor(doctorRequest);

        LocalDateTime dateTime = LocalDateTime.of(2026, 10, 10, 14, 0);
        AppointmentRequestDTO firstRequest = new AppointmentRequestDTO();
        firstRequest.setPatientId(patient.getId());
        firstRequest.setDoctorId(doctor.getId());
        firstRequest.setDateTime(dateTime);

        appointmentService.createAppointment(firstRequest);
        AppointmentRequestDTO secondRequest = new AppointmentRequestDTO();

        secondRequest.setPatientId(patient.getId());
        secondRequest.setDoctorId(doctor.getId());
        secondRequest.setDateTime(dateTime);

        assertThatThrownBy(() -> appointmentService.createAppointment(secondRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Doctor is not available");
    }
}
