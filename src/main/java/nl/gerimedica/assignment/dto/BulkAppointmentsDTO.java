package nl.gerimedica.assignment.dto;

import java.util.List;

import lombok.Data;

@Data
// created a DTO for bulk appointments.
public class BulkAppointmentsDTO {
    private List<String> reasons;
    private List<String> dates;
}
