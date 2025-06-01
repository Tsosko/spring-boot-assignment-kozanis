package nl.gerimedica.assignment.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nl.gerimedica.assignment.entities.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // This method finds appointments by their reason, ignoring case sensitivity.
    List<Appointment> findByReasonIgnoreCase(String reason);

    // This method finds appointments in descending order by date for a specific patient identified by their SSN.
    @Query("SELECT a FROM Appointment a WHERE a.patient.ssn = :ssn ORDER BY a.date DESC")
    List<Appointment> findByPatientSsnOrderByDateDesc(String ssn);
}
