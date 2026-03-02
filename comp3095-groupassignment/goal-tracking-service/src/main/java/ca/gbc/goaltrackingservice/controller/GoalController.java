package ca.gbc.goaltrackingservice.controller;

import ca.gbc.goaltrackingservice.dto.GoalRequest;
import ca.gbc.goaltrackingservice.dto.GoalResponse;
import ca.gbc.goaltrackingservice.dto.ProgressUpdateRequest;
import ca.gbc.goaltrackingservice.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Goal Tracking", description = "APIs for managing student wellness goals")
@SecurityRequirement(name = "bearerAuth")
public class GoalController {

    private final GoalService service;

    @PostMapping
    @Operation(summary = "Create a new goal", description = "Creates a new wellness goal for a student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Goal created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    })
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalRequest request) {
        GoalResponse response = service.createGoal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all goals", description = "Retrieves all goals, optionally filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goals retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status parameter"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<GoalResponse>> getAllOrFilteredGoals(
            @Parameter(description = "Filter by status: ACTIVE, COMPLETED, or ABANDONED")
            @RequestParam(required = false) String status) {
        List<GoalResponse> goals;
        if (status != null && !status.isEmpty()) {
            try {
                ca.gbc.goaltrackingservice.model.Goal.GoalStatus goalStatus =
                    ca.gbc.goaltrackingservice.model.Goal.GoalStatus.valueOf(status.toUpperCase().replace("-", "_"));
                goals = service.getGoalsByStatus(goalStatus);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            goals = service.getAllGoals();
        }
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID", description = "Retrieves a specific goal by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal found"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<GoalResponse> getGoalById(
            @Parameter(description = "Goal ID") @PathVariable String id) {
        GoalResponse response = service.getGoalById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/suggested-resources")
    @Operation(summary = "Get goal with suggested resources",
               description = "Retrieves a goal along with suggested wellness resources based on the goal category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal with resources found"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<GoalResponse> getGoalWithSuggestedResources(
            @Parameter(description = "Goal ID") @PathVariable String id) {
        GoalResponse response = service.getGoalWithSuggestedResources(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get goals by student", description = "Retrieves all goals for a specific student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goals retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<GoalResponse>> getGoalsByStudent(
            @Parameter(description = "Student ID") @PathVariable String studentId) {
        List<GoalResponse> goals = service.getGoalsByStudent(studentId);
        return ResponseEntity.ok(goals);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a goal", description = "Updates an existing goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal updated successfully"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<GoalResponse> updateGoal(
            @Parameter(description = "Goal ID") @PathVariable String id,
            @Valid @RequestBody GoalRequest request) {
        GoalResponse response = service.updateGoal(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/progress")
    @Operation(summary = "Update goal progress", description = "Updates the progress of a goal. Automatically marks as COMPLETED when progress reaches 100%")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Progress updated"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<GoalResponse> updateProgress(
            @Parameter(description = "Goal ID") @PathVariable String id,
            @Valid @RequestBody ProgressUpdateRequest request) {
        GoalResponse response = service.updateProgress(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark goal as complete", description = "Marks a goal as completed and sets progress to 100%")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal marked as complete"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<GoalResponse> completeGoal(
            @Parameter(description = "Goal ID") @PathVariable String id) {
        GoalResponse response = service.completeGoal(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a goal", description = "Deletes a goal by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Goal deleted"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteGoal(
            @Parameter(description = "Goal ID") @PathVariable String id) {
        service.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}
