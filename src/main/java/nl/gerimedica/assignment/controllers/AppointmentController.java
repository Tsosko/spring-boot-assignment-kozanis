package nl.gerimedica.assignment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import nl.gerimedica.assignment.dto.AppointmentDTO;
import nl.gerimedica.assignment.dto.BulkAppointmentsDTO;
import nl.gerimedica.assignment.entities.Appointment;
import nl.gerimedica.assignment.services.HospitalService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class AppointmentController {

    private final HospitalService hospitalService;

    @Autowired
    // Constructor-based dependency injection is preferred for better testability
    // and immutability.
    public AppointmentController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    /**
     * Example: {
     * "reasons": ["Checkup", "Follow-up", "X-Ray"],
     * "dates": ["2025-02-01", "2025-02-15", "2025-03-01"]
     * }
     */
    @PostMapping("/bulk-appointments")
    public ResponseEntity<?> createBulkAppointments(
            @RequestParam String patientName,
            @RequestParam String ssn,
            @RequestBody BulkAppointmentsDTO payload) {

        try {
            List<Appointment> created = hospitalService.bulkCreateAppointments(patientName, ssn, payload);
            return new ResponseEntity<>(AppointmentDTO.fromEntities(created), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error creating bulk appointments", e);
            return new ResponseEntity<>(Map.of("error", "Failed to create appointments"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/appointments-by-reason")
    public ResponseEntity<?> getAppointmentsByReason(@RequestParam String keyword) {
        try {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "Search keyword cannot be empty"), 
                HttpStatus.BAD_REQUEST);
        }  
        List<Appointment> found = hospitalService.getAppointmentsByReason(keyword);
        return new ResponseEntity<>(AppointmentDTO.fromEntities(found), HttpStatus.OK);
    } catch (Exception e) {
        log.error("Error retrieving appointments by reason", e);
        return new ResponseEntity<>(Map.of("error", "Failed to retrieve appointments"), 
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }

    // This should be a delete operation, not a get.
    @DeleteMapping("/delete-appointments")
    public ResponseEntity<?> deleteAppointmentsBySSN(@RequestParam String ssn) {
        try {
            hospitalService.deleteAppointmentsBySSN(ssn);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 is standard for successful deletions
        } catch (Exception e) {
            log.error("Error deleting appointments", e);
            return new ResponseEntity<>(Map.of("error", "Failed to delete appointments"), 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/appointments/latest")
    public ResponseEntity<?> getLatestAppointment(@RequestParam String ssn) {
        try {
            Appointment latest = hospitalService.findLatestAppointmentBySSN(ssn);
            if (latest == null) {
                return new ResponseEntity<>(Map.of("message", "No appointments found for SSN: " + ssn), 
                    HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(AppointmentDTO.fromEntity(latest), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
