package nl.gerimedica.assignment.services;

import lombok.extern.slf4j.Slf4j;
import nl.gerimedica.assignment.dto.BulkAppointmentsDTO;
import nl.gerimedica.assignment.entities.Appointment;
import nl.gerimedica.assignment.entities.Patient;
import nl.gerimedica.assignment.repositories.AppointmentRepository;
import nl.gerimedica.assignment.repositories.PatientRepository;
import nl.gerimedica.assignment.services.utils.HospitalUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
public class HospitalService {

    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;
    private final HospitalUtils hospitalUtils;

    @Autowired
    // Constructor-based dependency injection is preferred for better testability
    // and immutability.
    public HospitalService(PatientRepository patientRepo, AppointmentRepository appointmentRepo,
            HospitalUtils hospitalUtils) {
        this.patientRepo = patientRepo;
        this.appointmentRepo = appointmentRepo;
        this.hospitalUtils = hospitalUtils;
    }

    /**
     * Creates multiple appointments for a patient in a single transaction.
     * If the patient doesn't exist yet, a new patient record will be created first.
     * 
     * @param patientName Name of the patient
     * @param ssn         Social Security Number of the patient
     * @param payload     DTO containing lists of appointment reasons and dates
     * @return List of created appointments
     * @throws IllegalArgumentException if reasons or dates are null, empty, or have
     *                                  different sizes
     */
    @Transactional
    public List<Appointment> bulkCreateAppointments(String patientName, String ssn, BulkAppointmentsDTO payload) {
        Patient found = findPatientBySSN(ssn);
        if (found == null) {
            log.info("Creating new patient with SSN: {}", ssn);
            found = new Patient(patientName, ssn);
            savePatient(found);
        } else {
            log.info("Existing patient found, SSN: {}", found.getSsn());
        }

        List<String> reasons = payload.getReasons();
        List<String> dates = payload.getDates();

        if (reasons == null || dates == null || reasons.isEmpty() || dates.isEmpty()) {
            log.error("No reasons or dates provided for appointments.");
            throw new IllegalArgumentException("No reasons or dates provided for appointments.");
        } else if (reasons.size() != dates.size()) {
            log.error("Reasons and dates lists must be of the same size. Reasons: {}, Dates: {}", reasons.size(),
                    dates.size());
            throw new IllegalArgumentException("Reasons and dates must have the same number of entries.");
        }

        // Using IntStream to create a list of appointments based on the reasons and
        // dates provided.
        final Patient finalFound = found;
        List<Appointment> createdAppointments = IntStream.range(0, reasons.size())
                .mapToObj(i -> new Appointment(reasons.get(i), dates.get(i), finalFound))
                .toList();

        // Using the repository to save all appointments in one go is more efficient.
        appointmentRepo.saveAll(createdAppointments);

        for (Appointment appt : createdAppointments) {
            log.info("Created appointment for reason: {} [Date: {}] [Patient SSN: {}]", appt.getReason(),
                    appt.getDate(),
                    appt.getPatient().getSsn());
        }

        hospitalUtils.recordUsage("Bulk create appointments");

        return createdAppointments;
    }

    /**
     * Finds a patient by their Social Security Number.
     * 
     * @param ssn Social Security Number to search for
     * @return Patient if found, null otherwise
     * @throws IllegalArgumentException if SSN is null or empty
     */
    public Patient findPatientBySSN(String ssn) {
        // This method was inefficient because it fetched all patients and then filtered
        // them in memory.
        // Instead, I use the repository method that directly queries the database for
        // the patient by SSN.
        // Check if ssn is null or empty to avoid unnecessary database calls.
        if (ssn == null || ssn.trim().isEmpty()) {
            throw new IllegalArgumentException("SSN cannot be null or empty");
        }
        // The method finds a patient by their SSN. No need for fetching all patients
        // and then filter.
        return patientRepo.findBySsn(ssn).orElse(null);
    }

    /**
     * Saves a patient record to the database.
     * 
     * @param patient Patient entity to save
     */
    @Transactional
    void savePatient(Patient patient) {
        patientRepo.save(patient);
    }

    /**
     * Retrieves all appointments that match the given reason keyword.
     * The search is case-insensitive.
     * 
     * @param reasonKeyword Keyword to search for in appointment reasons
     * @return List of matching appointments
     */
    public List<Appointment> getAppointmentsByReason(String reasonKeyword) {

        // Created a method in the AppointmentRepository to find by reason ignoring
        // case.
        // This is more efficient than fetching all appointments and filtering them in
        // memory.
        List<Appointment> matched = appointmentRepo.findByReasonIgnoreCase(reasonKeyword);
        // The hospitalUtils has been replaced with a static method call.
        hospitalUtils.recordUsage("Get appointments by reason");
        return matched;
    }

    /**
     * Deletes all appointments for a patient identified by their SSN.
     * If the patient is not found or has no appointments, the method returns
     * without any action.
     * 
     * @param ssn Social Security Number of the patient whose appointments should be
     *            deleted
     */
    @Transactional
    public void deleteAppointmentsBySSN(String ssn) {

        Patient patient = findPatientBySSN(ssn);
        if (patient == null) {
            log.warn("No patient found with SSN: {}", ssn);
            return;
        }

        // Used accessor method instead of direct field access since appointments fields
        // are private.
        List<Appointment> appointments = patient.getAppointments();
        if (appointments == null || appointments.isEmpty()) {
            log.info("No appointments found for patient with SSN: {}", ssn);
            return;
        }
        appointmentRepo.deleteAll(appointments);

        hospitalUtils.recordUsage("Delete appointments by SSN");
    }

    /**
     * Finds the most recent appointment for a patient identified by their SSN.
     * Appointments are ordered by date in descending order, and the first one is
     * returned.
     * 
     * @param ssn Social Security Number of the patient
     * @return The most recent appointment, or null if no appointments are found
     * @throws IllegalArgumentException if SSN is null or empty
     */
    public Appointment findLatestAppointmentBySSN(String ssn) {

        // Check if ssn is null or empty to avoid unnecessary database calls.
        if (ssn == null || ssn.trim().isEmpty()) {
            throw new IllegalArgumentException("SSN cannot be null or empty");
        }

        // Using the repository method to find appointments by SSN in descending order.
        List<Appointment> appointments = appointmentRepo.findByPatientSsnOrderByDateDesc(ssn);
        if (appointments.isEmpty()) {
            log.info("No appointments found for SSN: {}", ssn);
            return null;
        }

        Appointment latest = appointments.get(0);
        log.info("Found latest appointment for SSN: {} on date: {}", ssn, latest.getDate());

        hospitalUtils.recordUsage("Find latest appointment by SSN");
        return latest;
    }
}
