package br.edu.infnet.hospital_system.patient;

import static br.edu.infnet.hospital_system.support.TestDataFactory.createPatient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import br.edu.infnet.hospital_system.HospitalSystemApplication;
import br.edu.infnet.hospital_system.PostgreSQLIntegrationTest;
import br.edu.infnet.hospital_system.config.TestcontainersConfiguration;
import br.edu.infnet.hospital_system.patient.model.Patient;
import br.edu.infnet.hospital_system.patient.repository.PatientRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(classes = HospitalSystemApplication.class)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class PatientRepositoryTest extends PostgreSQLIntegrationTest {

    private final PatientRepository patientRepository;

    public PatientRepositoryTest(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Test
    @DisplayName("Deve persistir e recuperar paciente por ID")
    void shouldPersistAndFindPatientById() {
        Patient patient = createPatient("12345678900");

        Patient savedPatient = patientRepository.saveAndFlush(patient);

        Patient foundPatient = patientRepository.findById(savedPatient.getId()).orElseThrow();

        assertThat(foundPatient.getId()).isNotNull();
        assertThat(foundPatient.getName()).isEqualTo("Elizabeth");
        assertThat(foundPatient.getSurname()).isEqualTo("Benneth");
        assertThat(foundPatient.getCpf()).isEqualTo("12345678900");
    }

    @Test
    @DisplayName("Deve encontrar paciente por CPF")
    void shouldFindPatientByCpf() {
        patientRepository.saveAndFlush(createPatient("98765432100"));
        Patient patient = patientRepository.findByCpf("98765432100").orElseThrow();
        assertThat(patient.getCpf()).isEqualTo("98765432100");
    }

    @Test
    @DisplayName("Deve verificar se CPF existe no banco de dados")
    void shouldVerifyWhetherCpfExists() {
        patientRepository.saveAndFlush(createPatient("45678912300"));
        boolean exists = patientRepository.existsByCpf("45678912300");
        boolean doesNotExist = patientRepository.existsByCpf("00000000000");

        assertThat(exists).isTrue();
        assertThat(doesNotExist).isFalse();
    }

    @Test
    @DisplayName("Deve rejeitar paciente com CPF duplicado")
    void shouldRejectDuplicateCpf() {
        patientRepository.saveAndFlush(createPatient("11122233344"));
        Patient duplicatePatient = createPatient("11122233344");

        assertThatThrownBy(() -> patientRepository.saveAndFlush(duplicatePatient))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Deve rejeitar paciente com CPF nulo")
    void shouldPopulateAuditingFields() {
        Patient patient = patientRepository.saveAndFlush(createPatient("22233344455"));

        assertThat(patient.getCreatedAt()).isNotNull();
        assertThat(patient.getUpdatedAt()).isNotNull();
        assertThat(patient.getVersion()).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar campos de auditoria e versão")
    void shouldUpdateAuditingAndVersionFields() {
        Patient patient = patientRepository.saveAndFlush(createPatient("33344455566"));

        Instant originalCreatedAt = patient.getCreatedAt();
        Instant originalUpdatedAt = patient.getUpdatedAt();
        Long originalVersion = patient.getVersion();

        patient.setPhonenumber("11888888888");
        Patient updatedPatient = patientRepository.saveAndFlush(patient);

        assertThat(updatedPatient.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(updatedPatient.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        assertThat(updatedPatient.getVersion()).isGreaterThan(originalVersion);
    }
}
