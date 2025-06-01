package nl.gerimedica.assignment.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nl.gerimedica.assignment.entities.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // This method finds a patient by their SSN (Social Security Number). No need for fetching all patients and then filtering.
    Optional<Patient> findBySsn(String ssn);
}
