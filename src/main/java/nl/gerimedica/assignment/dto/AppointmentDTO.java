package nl.gerimedica.assignment.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import nl.gerimedica.assignment.entities.Appointment;

@Data
// created a DTO for appointments.
public class AppointmentDTO {
    private Long id;
    private String reason;
    private String date;
    private String patientName;
    private String patientSSN;

    public static AppointmentDTO fromEntity(Appointment appointment) {
    if (appointment == null) return null;
    
    AppointmentDTO dto = new AppointmentDTO();
    dto.setId(appointment.getId());
    dto.setReason(appointment.getReason());
    dto.setDate(appointment.getDate());
    if (appointment.getPatient() != null) {
        dto.setPatientName(appointment.getPatient().getName());
        dto.setPatientSSN(appointment.getPatient().getSsn());
    }
    return dto;
}

public static List<AppointmentDTO> fromEntities(List<Appointment> appointments) {
    if (appointments == null) return List.of();
    return appointments.stream()
        .map(AppointmentDTO::fromEntity)
        .collect(Collectors.toList());
}
}