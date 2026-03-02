package ca.gbc.wellnessresourceservice.controller;

import ca.gbc.wellnessresourceservice.dto.WellnessResourceRequest;
import ca.gbc.wellnessresourceservice.dto.WellnessResourceResponse;
import ca.gbc.wellnessresourceservice.service.WellnessResourceService;
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
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Wellness Resources", description = "APIs for managing wellness resources (staff only)")
@SecurityRequirement(name = "bearerAuth")
public class WellnessResourceController {

    private final WellnessResourceService service;

    @PostMapping
    @Operation(summary = "Create a new resource", description = "Creates a new wellness resource (staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - staff role required")
    })
    public ResponseEntity<WellnessResourceResponse> createResource(
            @Valid @RequestBody WellnessResourceRequest request) {
        WellnessResourceResponse response = service.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all resources", description = "Retrieves all wellness resources, optionally filtered by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<WellnessResourceResponse>> getAllOrFilteredResources(
            @Parameter(description = "Filter by category (e.g., mental-health, fitness, nutrition)")
            @RequestParam(required = false) String category) {
        List<WellnessResourceResponse> resources;
        if (category != null && !category.isEmpty()) {
            resources = service.getResourcesByCategory(category);
        } else {
            resources = service.getAllResources();
        }
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resource by ID", description = "Retrieves a specific wellness resource by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource found"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<WellnessResourceResponse> getResourceById(
            @Parameter(description = "Resource ID") @PathVariable Long id) {
        WellnessResourceResponse response = service.getResourceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search resources", description = "Searches for wellness resources by keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<WellnessResourceResponse>> searchResources(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        List<WellnessResourceResponse> resources = service.searchResources(keyword);
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a resource", description = "Updates an existing wellness resource (staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - staff role required")
    })
    public ResponseEntity<WellnessResourceResponse> updateResource(
            @Parameter(description = "Resource ID") @PathVariable Long id,
            @Valid @RequestBody WellnessResourceRequest request) {
        WellnessResourceResponse response = service.updateResource(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resource", description = "Deletes a wellness resource by its ID (staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource deleted"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - staff role required")
    })
    public ResponseEntity<Void> deleteResource(
            @Parameter(description = "Resource ID") @PathVariable Long id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
