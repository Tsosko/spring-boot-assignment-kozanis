package nl.gerimedica.assignment.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
// The class was using public fields instead of getters and setters, which is not a good practice.
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reason;
    private String date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public Appointment(String reason, String date, Patient patient) {
        this.reason = reason;
        this.date = date;
        this.patient = patient;
    }
}
