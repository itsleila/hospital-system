package br.edu.infnet.hospital_system.doctor.service;

import br.edu.infnet.hospital_system.doctor.dto.DoctorResponseDTO;
import br.edu.infnet.hospital_system.doctor.dto.DoctorResquestDTO;
import br.edu.infnet.hospital_system.doctor.model.Doctor;
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
public class DoctorService {
    private static final List<Doctor> doctorList = new ArrayList<>();
    private Long idCounter = 1L;

    private Doctor toEntity(DoctorResquestDTO doctorResquestDTO) {
        Doctor doctorEntity = new Doctor();
        doctorEntity.setId(idCounter++);
        doctorEntity.setName(doctorResquestDTO.getName());
        doctorEntity.setSurname(doctorResquestDTO.getSurname());
        doctorEntity.setEmail(doctorResquestDTO.getEmail());
        doctorEntity.setCRM(doctorResquestDTO.getCRM());
        doctorEntity.setSpecialty(doctorResquestDTO.getSpecialty());
        return doctorEntity;
    }

    private DoctorResponseDTO toDTO(Doctor doctor) {
        DoctorResponseDTO doctorResponseDTO = new DoctorResponseDTO();
        doctorResponseDTO.setId(doctor.getId());
        doctorResponseDTO.setName(doctor.getName());
        doctorResponseDTO.setSurname(doctor.getSurname());
        doctorResponseDTO.setEmail(doctor.getEmail());
        doctorResponseDTO.setCRM(doctor.getCRM());
        doctorResponseDTO.setSpecialty(doctor.getSpecialty());
        return doctorResponseDTO;
    }

    public Boolean verifyCRM(String crm) {
        return doctorList.stream().anyMatch(doctor -> doctor.getCRM().equals(crm));
    }

    public Doctor verifyDoctorById(Long id) {
        return  doctorList.stream().filter(doctor -> doctor.getId().equals(id)).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not founr"));
    }


    public DoctorResponseDTO createDoctor(DoctorResquestDTO doctorResquestDTO){
        Boolean crmExists = verifyCRM(doctorResquestDTO.getCRM());
        if(crmExists){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,("CRM already exists"));
        }
        Doctor doctor = toEntity(doctorResquestDTO);
        doctorList.add(doctor);
        return toDTO(doctor);
    }

    public void deleteDoctorById(Long id){
        Doctor doctor = verifyDoctorById(id);
        doctorList.remove(doctor);
    }

    public DoctorResponseDTO findDoctorById(Long id){
        Doctor doctor = verifyDoctorById(id);
        return toDTO(doctor);
    }

    public List<DoctorResponseDTO> findAllDoctors(){
        return doctorList.stream().map(this::toDTO).collect(Collectors.toList());
    }


    public DoctorResponseDTO findDoctorByCRM(String crm){
        Doctor doctor = doctorList.stream().filter(d -> d.getCRM().equals(crm)).findFirst().
                orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "This CRM could not be found."));
        return toDTO(doctor);
    }

    public DoctorResponseDTO updateDoctorById(Long id, DoctorResquestDTO doctorResquestDTO){
        Doctor doctor = verifyDoctorById(id);
        doctor.setName(doctorResquestDTO.getName());
        doctor.setSurname(doctorResquestDTO.getSurname());
        doctor.setEmail(doctorResquestDTO.getEmail());
        doctor.setCRM(doctorResquestDTO.getCRM());
        doctor.setSpecialty(doctorResquestDTO.getSpecialty());
        return toDTO(doctor);
    }
}
