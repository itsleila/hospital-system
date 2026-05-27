package br.edu.infnet.hospital_system.doctor.controller;

import br.edu.infnet.hospital_system.doctor.dto.DoctorResponseDTO;
import br.edu.infnet.hospital_system.doctor.dto.DoctorResquestDTO;
import br.edu.infnet.hospital_system.doctor.model.Doctor;
import br.edu.infnet.hospital_system.doctor.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {
    private DoctorService doctorService;
    public DoctorController (DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
        List<DoctorResponseDTO> list = doctorService.findAllDoctors();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<DoctorResponseDTO> createDoctor(@RequestBody DoctorResquestDTO  doctorResquestDTO) {
        DoctorResponseDTO doctorResponseDTO = doctorService.createDoctor(doctorResquestDTO);
        return ResponseEntity.ok().body(doctorResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctorById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable Long id) {
        DoctorResponseDTO doctorResponseDTO = doctorService.findDoctorById(id);
        return ResponseEntity.ok().body(doctorResponseDTO);
    }

    @GetMapping("/crm")
    public ResponseEntity<DoctorResponseDTO> getDoctorsByCRM(@RequestParam String CRM) {
        DoctorResponseDTO doctor = doctorService.findDoctorByCRM(CRM);
        return ResponseEntity.ok().body(doctor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(@PathVariable Long id, @RequestBody DoctorResquestDTO doctorResquestDTO) {
        DoctorResponseDTO doctor = doctorService.updateDoctorById(id, doctorResquestDTO);
        return ResponseEntity.ok().body(doctor);
    }

}
