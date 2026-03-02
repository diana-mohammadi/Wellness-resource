package ca.gbc.goaltrackingservice.dto;

import ca.gbc.goaltrackingservice.model.Goal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalResponse {

    private String goalId;
    private String studentId;
    private String title;
    private String description;
    private String category;
    private LocalDate targetDate;
    private Goal.GoalStatus status;
    private Integer progress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<WellnessResourceDto> suggestedResources;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WellnessResourceDto {
        private Long resourceId;
        private String title;
        private String description;
        private String category;
        private String url;
    }
}
