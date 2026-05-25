package br.edu.infnet.hospital_system.patient.controller;

import br.edu.infnet.hospital_system.patient.dto.PatientRequestDTO;
import br.edu.infnet.hospital_system.patient.dto.PatientResponseDTO;
import br.edu.infnet.hospital_system.patient.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private PatientService patientService;

    public PatientController (PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> findAllPatients(){
        List<PatientResponseDTO> list = patientService.findAllPatients();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> savePatient(@RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO patient = patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(patient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id){
        patientService.deletePatientById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> findPatientById(@PathVariable Long id){
        PatientResponseDTO patient = patientService.findPatientById(id);
        return ResponseEntity.ok().body(patient);
    }

    @GetMapping("/cpf")
    public ResponseEntity<PatientResponseDTO> findPatientByCpf(@RequestParam String cpf){
        PatientResponseDTO patient = patientService.findPatientByCpf(cpf);
        return ResponseEntity.ok().body(patient);

    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable Long id, @RequestBody PatientRequestDTO patientRequestDTO){
        PatientResponseDTO patient = patientService.updatePatientById(id, patientRequestDTO);
        return ResponseEntity.ok().body(patient);
    }

}
