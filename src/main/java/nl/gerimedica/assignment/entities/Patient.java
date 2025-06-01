package nl.gerimedica.assignment.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
// The class was using public fields instead of getters and setters, which is not a good practice.
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String ssn;
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    public Patient(String name, String ssn) {
        this.name = name;
        this.ssn = ssn;
    }

}
