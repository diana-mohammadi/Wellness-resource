package ca.gbc.eventservice.service;

import ca.gbc.eventservice.client.WellnessResourceClient;
import ca.gbc.eventservice.dto.EventRequest;
import ca.gbc.eventservice.dto.EventResponse;
import ca.gbc.eventservice.dto.RegistrationRequest;
import ca.gbc.eventservice.dto.RegistrationResponse;
import ca.gbc.eventservice.model.Event;
import ca.gbc.eventservice.model.EventRegistration;
import ca.gbc.eventservice.repository.EventRegistrationRepository;
import ca.gbc.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository repository;
    private final EventRegistrationRepository registrationRepository;
    private final WellnessResourceClient wellnessResourceClient;

    @Transactional
    public EventResponse createEvent(EventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setCapacity(request.getCapacity());
        event.setResourceId(request.getResourceId());

        Event saved = repository.save(event);
        log.info("Created event with ID: {}", saved.getEventId());

        return mapToResponse(saved, false);
    }

    public List<EventResponse> getAllEvents() {
        log.info("Fetching all events");
        return repository.findAll().stream()
                .map(event -> mapToResponse(event, false))
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(Long id) {
        log.info("Fetching event with ID: {}", id);
        Event event = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        return mapToResponse(event, false);
    }

    public EventResponse getEventWithLinkedResource(Long id) {
        log.info("Fetching event with linked resource for ID: {}", id);
        Event event = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        return mapToResponse(event, true);
    }

    public List<EventResponse> getEventsByCategory(String category) {
        log.info("Fetching events for category: {}", category);
        return repository.findByCategory(category).stream()
                .map(event -> mapToResponse(event, false))
                .collect(Collectors.toList());
    }

    public List<EventResponse> getUpcomingEvents() {
        log.info("Fetching upcoming events");
        return repository.findUpcomingEvents(LocalDateTime.now()).stream()
                .map(event -> mapToResponse(event, false))
                .collect(Collectors.toList());
    }

    public List<EventResponse> getEventsByDate(LocalDateTime date) {
        log.info("Fetching events for date: {}", date);
        return repository.findEventsByDate(date).stream()
                .map(event -> mapToResponse(event, false))
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setCapacity(request.getCapacity());
        event.setResourceId(request.getResourceId());

        Event updated = repository.save(event);
        log.info("Updated event with ID: {}", id);

        return mapToResponse(updated, false);
    }

    @Transactional
    public void deleteEvent(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        repository.deleteById(id);
        log.info("Deleted event with ID: {}", id);
    }

    @Transactional
    public RegistrationResponse registerForEvent(Long eventId, RegistrationRequest request) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        if (registrationRepository.findByEventIdAndStudentId(eventId, request.getStudentId()).isPresent()) {
            throw new RuntimeException("Student is already registered for this event");
        }

        if (event.getCapacity() != null) {
            long registrationCount = registrationRepository.countByEventId(eventId);
            if (registrationCount >= event.getCapacity()) {
                throw new RuntimeException("Event is at full capacity");
            }
        }

        EventRegistration registration = new EventRegistration();
        registration.setEventId(eventId);
        registration.setStudentId(request.getStudentId());
        registration.setStudentName(request.getStudentName());
        registration.setStudentEmail(request.getStudentEmail());

        EventRegistration saved = registrationRepository.save(registration);
        log.info("Student {} registered for event {}", request.getStudentId(), eventId);

        return RegistrationResponse.builder()
                .registrationId(saved.getRegistrationId())
                .eventId(saved.getEventId())
                .studentId(saved.getStudentId())
                .studentName(saved.getStudentName())
                .studentEmail(saved.getStudentEmail())
                .registeredAt(saved.getRegisteredAt())
                .message("Successfully registered for event: " + event.getTitle())
                .build();
    }

    /**
     * Recommends events for a student based on their completed goal category.
     * This method is called when a GoalCompletedEvent is received from Kafka.
     */
    public void recommendEventsForCompletedGoal(String studentId, String category) {
        log.info("Finding recommended events for student: {} in category: {}", studentId, category);

        List<Event> relevantEvents = repository.findByCategory(category);
        List<Event> upcomingEvents = relevantEvents.stream()
                .filter(e -> e.getEventDate() != null && e.getEventDate().isAfter(LocalDateTime.now()))
                .toList();

        log.info("Found {} upcoming events in category {} for student {}",
                upcomingEvents.size(), category, studentId);

        upcomingEvents.forEach(evt ->
                log.info("Recommended event for student {}: {} on {}",
                        studentId, evt.getTitle(), evt.getEventDate()));
    }

    private EventResponse mapToResponse(Event event, boolean includeLinkedResource) {
        EventResponse.WellnessResourceDto linkedResource = null;

        if (includeLinkedResource && event.getResourceId() != null) {
            linkedResource = wellnessResourceClient.getResourceById(event.getResourceId());
        }

        return EventResponse.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .capacity(event.getCapacity())
                .resourceId(event.getResourceId())
                .linkedResource(linkedResource)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
