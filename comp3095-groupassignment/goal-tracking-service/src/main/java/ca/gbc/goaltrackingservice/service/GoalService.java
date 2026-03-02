package ca.gbc.goaltrackingservice.service;

import ca.gbc.goaltrackingservice.client.WellnessResourceClient;
import ca.gbc.goaltrackingservice.dto.GoalRequest;
import ca.gbc.goaltrackingservice.dto.GoalResponse;
import ca.gbc.goaltrackingservice.dto.ProgressUpdateRequest;
import ca.gbc.goaltrackingservice.event.GoalCompletedEvent;
import ca.gbc.goaltrackingservice.event.GoalEventProducer;
import ca.gbc.goaltrackingservice.model.Goal;
import ca.gbc.goaltrackingservice.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {

    private final GoalRepository repository;
    private final WellnessResourceClient wellnessResourceClient;
    private final GoalEventProducer goalEventProducer;

    public GoalResponse createGoal(GoalRequest request) {
        Goal goal = new Goal();
        goal.setStudentId(request.getStudentId());
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setCategory(request.getCategory());
        goal.setTargetDate(request.getTargetDate());
        goal.setStatus(request.getStatus() != null ? request.getStatus() : Goal.GoalStatus.ACTIVE);
        goal.setProgress(request.getProgress() != null ? request.getProgress() : 0);

        Goal saved = repository.save(goal);
        log.info("Created goal with ID: {}", saved.getGoalId());

        return mapToResponse(saved);
    }

    public List<GoalResponse> getAllGoals() {
        log.info("Fetching all goals");
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<GoalResponse> getGoalsByStatus(Goal.GoalStatus status) {
        log.info("Fetching goals with status: {}", status);
        return repository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public GoalResponse getGoalById(String id) {
        log.info("Fetching goal with ID: {}", id);
        Goal goal = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
        return mapToResponse(goal);
    }

    public GoalResponse getGoalWithSuggestedResources(String id) {
        log.info("Fetching goal with suggested resources for ID: {}", id);
        Goal goal = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));

        GoalResponse response = mapToResponse(goal);

        List<GoalResponse.WellnessResourceDto> suggestedResources =
                wellnessResourceClient.getResourcesByCategory(goal.getCategory());
        response.setSuggestedResources(suggestedResources);

        return response;
    }

    public List<GoalResponse> getGoalsByStudent(String studentId) {
        log.info("Fetching goals for student: {}", studentId);
        return repository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public GoalResponse updateGoal(String id, GoalRequest request) {
        Goal goal = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));

        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setCategory(request.getCategory());
        goal.setTargetDate(request.getTargetDate());
        goal.setStatus(request.getStatus());
        goal.setProgress(request.getProgress());

        Goal updated = repository.save(goal);
        log.info("Updated goal with ID: {}", id);

        // Publish event if goal is now completed
        if (updated.getStatus() == Goal.GoalStatus.COMPLETED) {
            publishGoalCompletedEvent(updated);
        }

        return mapToResponse(updated);
    }

    public GoalResponse updateProgress(String id, ProgressUpdateRequest request) {
        Goal goal = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));

        boolean wasNotCompleted = goal.getStatus() != Goal.GoalStatus.COMPLETED;

        if (request.getProgress() != null) {
            goal.setProgress(request.getProgress());
        }
        if (request.getStatus() != null) {
            goal.setStatus(request.getStatus());
        }

        if (goal.getProgress() >= 100) {
            goal.setStatus(Goal.GoalStatus.COMPLETED);
        }

        Goal updated = repository.save(goal);
        log.info("Updated progress for goal with ID: {}", id);

        // Publish event if goal just became completed
        if (wasNotCompleted && updated.getStatus() == Goal.GoalStatus.COMPLETED) {
            publishGoalCompletedEvent(updated);
        }

        return mapToResponse(updated);
    }

    public void deleteGoal(String id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Goal not found with id: " + id);
        }
        repository.deleteById(id);
        log.info("Deleted goal with ID: {}", id);
    }

    public GoalResponse completeGoal(String id) {
        Goal goal = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));

        goal.setStatus(Goal.GoalStatus.COMPLETED);
        goal.setProgress(100);

        Goal updated = repository.save(goal);
        log.info("Marked goal {} as completed", id);

        // Publish GoalCompletedEvent to Kafka
        publishGoalCompletedEvent(updated);

        return mapToResponse(updated);
    }

    private void publishGoalCompletedEvent(Goal goal) {
        GoalCompletedEvent event = GoalCompletedEvent.builder()
                .goalId(goal.getGoalId())
                .studentId(goal.getStudentId())
                .title(goal.getTitle())
                .category(goal.getCategory())
                .completedAt(LocalDateTime.now())
                .build();
        goalEventProducer.sendGoalCompletedEvent(event);
    }

    private GoalResponse mapToResponse(Goal goal) {
        return GoalResponse.builder()
                .goalId(goal.getGoalId())
                .studentId(goal.getStudentId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .category(goal.getCategory())
                .targetDate(goal.getTargetDate())
                .status(goal.getStatus())
                .progress(goal.getProgress())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }
}
