package br.edu.infnet.hospital_system.support;

import br.edu.infnet.hospital_system.appointment.model.Appointment;
import br.edu.infnet.hospital_system.appointment.model.AppointmentStatus;
import br.edu.infnet.hospital_system.doctor.model.Doctor;
import br.edu.infnet.hospital_system.patient.model.Gender;
import br.edu.infnet.hospital_system.patient.model.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Patient createPatient(String cpf) {
        Patient patient = new Patient();

        patient.setName("Elizabeth");
        patient.setSurname("Benneth");
        patient.setCpf(cpf);
        patient.setBirthdate(LocalDate.of(1999, 2, 20));
        patient.setGender(Gender.FEMALE);
        patient.setPhonenumber("33991999999");

        return patient;
    }

    public static Doctor createDoctor(String crm) {
        Doctor doctor = new Doctor();

        doctor.setName("Fitzwilliam");
        doctor.setSurname("Darcy");
        doctor.setEmail("darcy.fitzwilliam@hospital.com");
        doctor.setCrm(crm);
        doctor.setSpecialty("Cardiology");

        return doctor;
    }

    public static Appointment createAppointment(Patient patient, Doctor doctor, LocalDateTime dateTime) {
        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDateTime(dateTime);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointment;
    }
}