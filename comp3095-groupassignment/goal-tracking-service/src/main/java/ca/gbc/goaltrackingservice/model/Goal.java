package ca.gbc.goaltrackingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    private String goalId;

    private String studentId;
    private String title;
    private String description;
    private String category;
    private LocalDate targetDate;
    private GoalStatus status;
    private Integer progress;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum GoalStatus {
        ACTIVE,
        COMPLETED,
        ABANDONED
    }
}
