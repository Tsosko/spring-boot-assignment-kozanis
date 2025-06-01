package nl.gerimedica.assignment.repositories;

import nl.gerimedica.assignment.entities.Patient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void findBySsn_WithExistingSSN_ShouldReturnPatient() {
        Patient patient = new Patient("patient1", "123-45-6789");
        patientRepository.save(patient);

        Optional<Patient> found = patientRepository.findBySsn("123-45-6789");

        assertTrue(found.isPresent());
        assertEquals("patient1", found.get().getName());
    }

    @Test
    void findBySsn_WithNonExistentSSN_ShouldReturnEmptyOptional() {
        Optional<Patient> found = patientRepository.findBySsn("999-99-9999");

        assertFalse(found.isPresent());
    }
}