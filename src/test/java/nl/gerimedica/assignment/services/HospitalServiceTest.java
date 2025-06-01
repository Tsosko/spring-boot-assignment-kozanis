package nl.gerimedica.assignment.services;

import nl.gerimedica.assignment.dto.BulkAppointmentsDTO;
import nl.gerimedica.assignment.entities.Appointment;
import nl.gerimedica.assignment.entities.Patient;
import nl.gerimedica.assignment.repositories.AppointmentRepository;
import nl.gerimedica.assignment.repositories.PatientRepository;
import nl.gerimedica.assignment.services.utils.HospitalUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;
    
    @Mock
    private HospitalUtils hospitalUtils;

    @InjectMocks
    private HospitalService hospitalService;

    private Patient testPatient;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        testPatient = new Patient("Test Patient", "123-45-6789");
        testAppointment = new Appointment("Checkup", "2025-01-15", testPatient);
    }

    @Test
    void findPatientBySSN_ShouldReturnPatient() {
        when(patientRepository.findBySsn("123-45-6789")).thenReturn(Optional.of(testPatient));
        
        Patient result = hospitalService.findPatientBySSN("123-45-6789");
        
        assertNotNull(result);
        assertEquals("Test Patient", result.getName());
    }

    @Test
    void savePatient_ShouldCallRepository() {
        hospitalService.savePatient(testPatient);
        
        verify(patientRepository).save(testPatient);
    }

    @Test
    void getAppointmentsByReason_ShouldReturnAppointments() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByReasonIgnoreCase("Checkup")).thenReturn(appointments);
        
        List<Appointment> result = hospitalService.getAppointmentsByReason("Checkup");
        
        assertEquals(1, result.size());
        verify(hospitalUtils).recordUsage("Get appointments by reason");
    }

    @Test
    void bulkCreateAppointments_WithNewPatient_ShouldCreateAppointments() {
        BulkAppointmentsDTO dto = new BulkAppointmentsDTO();
        dto.setReasons(Arrays.asList("Reason1"));
        dto.setDates(Arrays.asList("2025-03-15"));
        
        when(patientRepository.findBySsn(anyString())).thenReturn(Optional.empty());
        
        List<Appointment> result = hospitalService.bulkCreateAppointments("New Patient", "999-88-7777", dto);
        
        assertEquals(1, result.size());
        verify(patientRepository).save(any(Patient.class));
        verify(appointmentRepository).saveAll(anyList());
    }

    @Test
    void deleteAppointmentsBySSN_WithExistingPatient_ShouldDeleteAppointments() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        testPatient.setAppointments(appointments);
        
        when(patientRepository.findBySsn("123-45-6789")).thenReturn(Optional.of(testPatient));
        
        hospitalService.deleteAppointmentsBySSN("123-45-6789");
        
        verify(appointmentRepository).deleteAll(appointments);
        verify(hospitalUtils).recordUsage("Delete appointments by SSN");
    }

    @Test
    void findLatestAppointmentBySSN_ShouldReturnLatestAppointment() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByPatientSsnOrderByDateDesc("123-45-6789")).thenReturn(appointments);
        
        Appointment result = hospitalService.findLatestAppointmentBySSN("123-45-6789");
        
        assertNotNull(result);
        assertEquals("Checkup", result.getReason());
        verify(hospitalUtils).recordUsage("Find latest appointment by SSN");
    }
}