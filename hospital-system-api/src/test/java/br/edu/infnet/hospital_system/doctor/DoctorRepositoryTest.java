package br.edu.infnet.hospital_system.doctor;

import static br.edu.infnet.hospital_system.support.TestDataFactory.createDoctor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.edu.infnet.hospital_system.HospitalSystemApplication;
import br.edu.infnet.hospital_system.PostgreSQLIntegrationTest;
import br.edu.infnet.hospital_system.config.TestcontainersConfiguration;
import br.edu.infnet.hospital_system.doctor.model.Doctor;
import br.edu.infnet.hospital_system.doctor.repository.DoctorRepository;

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
class DoctorRepositoryTest extends PostgreSQLIntegrationTest {

    private final DoctorRepository doctorRepository;

    public DoctorRepositoryTest(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Test
    @DisplayName("Deve persistir e buscar médico por ID")
    void shouldPersistAndFindDoctorById() {
        Doctor doctor = doctorRepository.saveAndFlush(createDoctor("CRM-SP-1000"));
        Doctor foundDoctor = doctorRepository.findById(doctor.getId()).orElseThrow();

        assertThat(foundDoctor.getId()).isNotNull();
        assertThat(foundDoctor.getName()).isEqualTo("Fitzwilliam");
        assertThat(foundDoctor.getCrm()).isEqualTo("CRM-SP-1000");
        assertThat(foundDoctor.getSpecialty()).isEqualTo("Cardiology");
    }

    @Test
    @DisplayName("Deve buscar médico por CRM ignorando maiúsculas e minúsculas")
    void shouldFindDoctorByCrmIgnoringCase() {
        doctorRepository.saveAndFlush(createDoctor("CRM-SP-2000"));
        Doctor doctor = doctorRepository.findByCrmIgnoreCase("crm-sp-2000").orElseThrow();

        assertThat(doctor.getCrm()).isEqualTo("CRM-SP-2000");
    }

    @Test
    @DisplayName("Deve verificar se CRM existe ignorando maiúsculas e minúsculas")
    void shouldVerifyWhetherCrmExistsIgnoringCase() {
        doctorRepository.saveAndFlush(createDoctor("CRM-SP-3000"));

        assertThat(doctorRepository.existsByCrmIgnoreCase("crm-sp-3000")).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar CRM duplicado")
    void shouldRejectDuplicateCrm() {
        doctorRepository.saveAndFlush(createDoctor("CRM-SP-4000"));

        Doctor duplicateDoctor = createDoctor("CRM-SP-4000");

        assertThatThrownBy(() -> doctorRepository.saveAndFlush(duplicateDoctor))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
