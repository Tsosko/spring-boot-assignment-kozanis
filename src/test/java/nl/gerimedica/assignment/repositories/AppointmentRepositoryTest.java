package nl.gerimedica.assignment.repositories;

import nl.gerimedica.assignment.entities.Appointment;
import nl.gerimedica.assignment.entities.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private PatientRepository patientRepository;

    @Test
    void findByReasonIgnoreCase_ShouldReturnMatchingAppointments() {
        Patient patient = new Patient("patient1", "123-45-6789");
        patientRepository.save(patient);
        
        Appointment appointment1 = new Appointment("Checkup", "2025-01-15", patient);
        Appointment appointment2 = new Appointment("Follow-up", "2025-02-15", patient);
        Appointment appointment3 = new Appointment("checkup", "2025-03-15", patient);
        
        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);
        appointmentRepository.save(appointment3);
        
        List<Appointment> result = appointmentRepository.findByReasonIgnoreCase("checkup");
        
        assertEquals(2, result.size());
    }

    @Test
    void findByPatientSsnOrderByDateDesc_ShouldReturnAppointmentsInDescendingOrder() {
        
        // Create patient with SSN
        Patient patient = new Patient("patient1", "423-54-1345");
        patientRepository.save(patient);
        
        // Create another patient to verify filtering works correctly
        Patient otherPatient = new Patient("patien2", "987-65-4321");
        patientRepository.save(otherPatient);
        
        // Create appointments with different dates for the same patient
        Appointment appointment1 = new Appointment("First Visit", "2025-01-15", patient);
        Appointment appointment2 = new Appointment("Second Visit", "2025-03-20", patient);
        Appointment appointment3 = new Appointment("Third Visit", "2025-02-10", patient);
        
        // Create an appointment for the other patient
        Appointment otherAppointment = new Appointment("Other Patient Visit", "2025-02-05", otherPatient);
        
        // Save all appointments
        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);
        appointmentRepository.save(appointment3);
        appointmentRepository.save(otherAppointment);
        

        List<Appointment> result = appointmentRepository.findByPatientSsnOrderByDateDesc("423-54-1345");
        
        // Check that we only got appointments for the correct patient
        assertEquals(3, result.size());
        
        // Check that they are in the correct order (newest first)
        assertEquals("Second Visit", result.get(0).getReason()); // 2025-03-20
        assertEquals("Third Visit", result.get(1).getReason());  // 2025-02-10
        assertEquals("First Visit", result.get(2).getReason());  // 2025-01-15
    }
}