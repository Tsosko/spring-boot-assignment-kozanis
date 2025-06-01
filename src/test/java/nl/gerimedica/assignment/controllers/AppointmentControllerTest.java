package nl.gerimedica.assignment.controllers;

import nl.gerimedica.assignment.dto.BulkAppointmentsDTO;
import nl.gerimedica.assignment.entities.Appointment;
import nl.gerimedica.assignment.services.HospitalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private HospitalService hospitalService;

    @InjectMocks
    private AppointmentController appointmentController;

    @Test
    void getAppointmentsByReason_ShouldReturnAppointments() {
        List<Appointment> appointments = Arrays.asList(new Appointment(), new Appointment());
        when(hospitalService.getAppointmentsByReason("Checkup")).thenReturn(appointments);
        
        ResponseEntity<?> response = appointmentController.getAppointmentsByReason("Checkup");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAppointmentsByReason_WithEmptyKeyword_ShouldReturnBadRequest() {
        // Act
        ResponseEntity<?> response = appointmentController.getAppointmentsByReason("");
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createBulkAppointments_ShouldCreateAppointments() {
        // Arrange
        List<String> reasons = Arrays.asList("Checkup", "Follow-up");
        List<String> dates = Arrays.asList("2025-01-15", "2025-02-15");
        BulkAppointmentsDTO payload = new BulkAppointmentsDTO();
        payload.setReasons(reasons);
        payload.setDates(dates);
        
        List<Appointment> appointments = Arrays.asList(new Appointment(), new Appointment());
        when(hospitalService.bulkCreateAppointments("patient1", "123-45-6789", payload))
            .thenReturn(appointments);
            
        // Act
        ResponseEntity<?> response = appointmentController
            .createBulkAppointments("patient1", "123-45-6789", payload);
            
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}