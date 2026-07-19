package br.edu.infnet.hospital_system.patient.service;

import java.util.List;

import br.edu.infnet.hospital_system.patient.dto.PatientRequestDTO;
import br.edu.infnet.hospital_system.patient.dto.PatientResponseDTO;
import br.edu.infnet.hospital_system.patient.model.Patient;
import br.edu.infnet.hospital_system.patient.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    private Patient toEntity(PatientRequestDTO dto) {
        Patient patient = new Patient();

        patient.setName(dto.getName());
        patient.setSurname(dto.getSurname());
        patient.setCpf(dto.getCpf());
        patient.setBirthdate(dto.getBirthdate());
        patient.setGender(dto.getGender());
        patient.setPhonenumber(dto.getPhonenumber());

        return patient;
    }

    private PatientResponseDTO toDTO(Patient patient) {
        PatientResponseDTO dto = new PatientResponseDTO();

        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setSurname(patient.getSurname());
        dto.setCpf(patient.getCpf());
        dto.setBirthdate(patient.getBirthdate());
        dto.setGender(patient.getGender());
        dto.setPhonenumber(patient.getPhonenumber());

        return dto;
    }

    @Transactional(readOnly = true)
    public boolean verifyCpf(String cpf) {
        return patientRepository.existsByCpf(cpf);
    }

    @Transactional(readOnly = true)
    public Patient verifyPatientById(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequest) {

        if (patientRepository.existsByCpf(patientRequest.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF already exists");
        }

        Patient patient = toEntity(patientRequest);
        Patient savedPatient = patientRepository.save(patient);

        return toDTO(savedPatient);
    }

    public void deletePatientById(Long id) {
        Patient patient = verifyPatientById(id);
        patientRepository.delete(patient);
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO findPatientById(Long id) {
        return toDTO(verifyPatientById(id));
    }

    @Transactional(readOnly = true)
    public List<PatientResponseDTO> findAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO findPatientByCpf(String cpf) {
        Patient patient = patientRepository.findByCpf(cpf).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This CPF could not be found"));

        return toDTO(patient);
    }

    public PatientResponseDTO updatePatientById(
            Long id,
            PatientRequestDTO dto) {

        Patient patient = verifyPatientById(id);

        boolean cpfChanged = !patient.getCpf().equals(dto.getCpf());

        if (cpfChanged && patientRepository.existsByCpf(dto.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF already exists");
        }

        patient.setName(dto.getName());
        patient.setSurname(dto.getSurname());
        patient.setCpf(dto.getCpf());
        patient.setBirthdate(dto.getBirthdate());
        patient.setGender(dto.getGender());
        patient.setPhonenumber(dto.getPhonenumber());

        Patient updatedPatient = patientRepository.save(patient);

        return toDTO(updatedPatient);
    }
}
