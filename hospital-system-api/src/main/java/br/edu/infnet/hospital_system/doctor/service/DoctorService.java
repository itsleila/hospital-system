package br.edu.infnet.hospital_system.doctor.service;

import java.util.List;

import br.edu.infnet.hospital_system.doctor.dto.DoctorResponseDTO;
import br.edu.infnet.hospital_system.doctor.dto.DoctorResquestDTO;
import br.edu.infnet.hospital_system.doctor.dto.DoctorUpdateRequestDTO;
import br.edu.infnet.hospital_system.doctor.model.Doctor;
import br.edu.infnet.hospital_system.doctor.repository.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    private Doctor toEntity(DoctorResquestDTO dto) {
        Doctor doctor = new Doctor();

        doctor.setName(dto.getName());
        doctor.setSurname(dto.getSurname());
        doctor.setEmail(dto.getEmail());
        doctor.setCrm(dto.getCRM());
        doctor.setSpecialty(dto.getSpecialty());

        return doctor;
    }

    private DoctorResponseDTO toDTO(Doctor doctor) {
        DoctorResponseDTO dto = new DoctorResponseDTO();

        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setSurname(doctor.getSurname());
        dto.setEmail(doctor.getEmail());
        dto.setCRM(doctor.getCrm());
        dto.setSpecialty(doctor.getSpecialty());

        return dto;
    }

    @Transactional(readOnly = true)
    public boolean verifyCRM(String crm) {
        return doctorRepository.existsByCrmIgnoreCase(crm);
    }

    @Transactional(readOnly = true)
    public Doctor verifyDoctorById(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
    }

    public DoctorResponseDTO createDoctor(
            DoctorResquestDTO doctorRequest) {

        if (doctorRepository.existsByCrmIgnoreCase(doctorRequest.getCRM())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CRM already exists");
        }

        Doctor doctor = toEntity(doctorRequest);
        Doctor savedDoctor = doctorRepository.save(doctor);

        return toDTO(savedDoctor);
    }

    public void deleteDoctorById(Long id) {
        Doctor doctor = verifyDoctorById(id);
        doctorRepository.delete(doctor);
    }

    @Transactional(readOnly = true)
    public DoctorResponseDTO findDoctorById(Long id) {
        return toDTO(verifyDoctorById(id));
    }

    @Transactional(readOnly = true)
    public List<DoctorResponseDTO> findAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DoctorResponseDTO findDoctorByCRM(String crm) {
        Doctor doctor = doctorRepository.findByCrmIgnoreCase(crm).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This CRM could not be found"));

        return toDTO(doctor);
    }

    public DoctorResponseDTO updateDoctorById(Long id, DoctorUpdateRequestDTO dto
    ) {

        Doctor doctor = verifyDoctorById(id);

        doctor.setName(dto.getName());
        doctor.setSurname(dto.getSurname());
        doctor.setEmail(dto.getEmail());
        doctor.setSpecialty(dto.getSpecialty());

        Doctor updatedDoctor = doctorRepository.save(doctor);

        return toDTO(updatedDoctor);
    }
}