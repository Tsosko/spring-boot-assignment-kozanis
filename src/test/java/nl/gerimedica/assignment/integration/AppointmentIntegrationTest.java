package nl.gerimedica.assignment.integration;

import nl.gerimedica.assignment.dto.BulkAppointmentsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AppointmentIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createAndRetrieveAppointment() {
        //Create an appointment
        BulkAppointmentsDTO payload = new BulkAppointmentsDTO();
        payload.setReasons(Arrays.asList("Integration Test Appointment"));
        payload.setDates(Arrays.asList("2025-12-31"));
        
        String createUrl = "http://localhost:" + port + "/api/bulk-appointments?patientName=Test Patient&ssn=999-88-7777";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<BulkAppointmentsDTO> request = new HttpEntity<>(payload, headers);
        
        ResponseEntity<Object> createResponse = restTemplate.postForEntity(
            createUrl, request, Object.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        
        //Retrieve the appointment by reason
        String retrieveUrl = "http://localhost:" + port + "/api/appointments-by-reason?keyword=Integration Test Appointment";
        
        ResponseEntity<Object> retrieveResponse = restTemplate.getForEntity(
            retrieveUrl, Object.class);
        
        assertEquals(HttpStatus.OK, retrieveResponse.getStatusCode());
        
        //Verify the latest appointment
        String latestUrl = "http://localhost:" + port + "/api/appointments/latest?ssn=999-88-7777";
        
        ResponseEntity<Map> latestResponse = restTemplate.getForEntity(
            latestUrl, Map.class);
        
        assertEquals(HttpStatus.OK, latestResponse.getStatusCode());
        assertEquals("Integration Test Appointment", latestResponse.getBody().get("reason"));
    }
}