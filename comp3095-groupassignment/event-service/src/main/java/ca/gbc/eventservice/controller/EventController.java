package ca.gbc.eventservice.controller;

import ca.gbc.eventservice.dto.EventRequest;
import ca.gbc.eventservice.dto.EventResponse;
import ca.gbc.eventservice.dto.RegistrationRequest;
import ca.gbc.eventservice.dto.RegistrationResponse;
import ca.gbc.eventservice.service.EventService;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "APIs for managing wellness events and registrations")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    private final EventService service;

    @PostMapping
    @Operation(summary = "Create a new event", description = "Creates a new wellness event (staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - staff role required")
    })
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse response = service.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all events", description = "Retrieves all events, optionally filtered by date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<EventResponse>> getAllOrFilteredEvents(
            @Parameter(description = "Filter by date (format: YYYY-MM-DD)")
            @RequestParam(required = false) String date) {
        List<EventResponse> events;
        if (date != null && !date.isEmpty()) {
            try {
                java.time.LocalDate localDate = java.time.LocalDate.parse(date);
                java.time.LocalDateTime dateTime = localDate.atStartOfDay();
                events = service.getEventsByDate(dateTime);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            events = service.getAllEvents();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieves a specific event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<EventResponse> getEventById(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        EventResponse response = service.getEventById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/resources")
    @Operation(summary = "Get event with linked resources",
               description = "Retrieves an event along with its linked wellness resources")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event with resources found"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<EventResponse> getEventWithLinkedResource(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        EventResponse response = service.getEventWithLinkedResource(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get events by category", description = "Retrieves all events in a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<EventResponse>> getEventsByCategory(
            @Parameter(description = "Event category") @PathVariable String category) {
        List<EventResponse> events = service.getEventsByCategory(category);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events", description = "Retrieves all future events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upcoming events retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        List<EventResponse> events = service.getUpcomingEvents();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/{id}/register")
    @Operation(summary = "Register for an event", description = "Registers a student for an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Already registered or event at capacity"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<RegistrationResponse> registerForEvent(
            @Parameter(description = "Event ID") @PathVariable Long id,
            @Valid @RequestBody RegistrationRequest request) {
        RegistrationResponse response = service.registerForEvent(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an event", description = "Updates an existing event (staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - staff role required")
    })
    public ResponseEntity<EventResponse> updateEvent(
            @Parameter(description = "Event ID") @PathVariable Long id,
            @Valid @RequestBody EventRequest request) {
        EventResponse response = service.updateEvent(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event", description = "Deletes an event by its ID (staff only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - staff role required")
    })
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        service.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
