package ca.gbc.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {

    private Long registrationId;
    private Long eventId;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private LocalDateTime registeredAt;
    private String message;
}
