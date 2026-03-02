package ca.gbc.goaltrackingservice.dto;

import ca.gbc.goaltrackingservice.model.Goal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest {

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    private LocalDate targetDate;

    private Goal.GoalStatus status;

    @Min(0)
    @Max(100)
    private Integer progress;
}
