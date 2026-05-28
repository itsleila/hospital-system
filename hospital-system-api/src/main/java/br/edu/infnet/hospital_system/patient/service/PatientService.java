package br.edu.infnet.hospital_system.patient.service;

import br.edu.infnet.hospital_system.patient.dto.PatientRequestDTO;
import br.edu.infnet.hospital_system.patient.dto.PatientResponseDTO;
import br.edu.infnet.hospital_system.patient.model.Patient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {
    private static final List<Patient> patientList = new ArrayList<>();
    private Long idCounter = 1L;

    private Patient toEntity(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setId(idCounter++);
        patient.setName(patientRequestDTO.getName());
        patient.setSurname(patientRequestDTO.getSurname());
        patient.setCpf(patientRequestDTO.getCpf());
        patient.setBirthdate(patientRequestDTO.getBirthdate());
        patient.setGender(patientRequestDTO.getGender());
        patient.setPhonenumber(patientRequestDTO.getPhonenumber());

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

    public Boolean verifyCpf(String cpf) {
        return patientList.stream().anyMatch(patient -> patient.getCpf().equals(cpf));
    }

    public Patient verifyPatientById(Long id) {
        return  patientList.stream().filter(patient -> patient.getId().equals(id)).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    public PatientResponseDTO createPatient (PatientRequestDTO patientRequest){
        Boolean patientExists = verifyCpf(patientRequest.getCpf());
        if(patientExists){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,("CPF already exists"));
        }
        Patient patient = toEntity(patientRequest);
        patientList.add(patient);
        return toDTO(patient);
    }

    public void deletePatientById(Long id){
        Patient patient = verifyPatientById(id);
        patientList.remove(patient);
    }

    public PatientResponseDTO findPatientById(Long id){
        Patient patient  = verifyPatientById(id);
        return toDTO(patient);
    }

    public List<PatientResponseDTO> findAllPatients(){
        return patientList.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PatientResponseDTO findPatientByCpf(String cpf){
        Patient patient = patientList.stream().filter(p -> p.getCpf().equals(cpf)).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This CPF could not be found."));
        return toDTO(patient);
    }

    public PatientResponseDTO updatePatientById(Long id, PatientRequestDTO patientRequestDTO){
        Patient patient = verifyPatientById(id);
        patient.setId(id);
        patient.setName(patientRequestDTO.getName());
        patient.setSurname(patientRequestDTO.getSurname());
        patient.setBirthdate(patientRequestDTO.getBirthdate());
        patient.setGender(patientRequestDTO.getGender());
        patient.setPhonenumber(patientRequestDTO.getPhonenumber());
        return toDTO(patient);
    }

}
