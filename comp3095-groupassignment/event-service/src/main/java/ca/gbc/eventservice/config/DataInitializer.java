package ca.gbc.eventservice.config;

import ca.gbc.eventservice.model.Event;
import ca.gbc.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final EventRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            log.info("Initializing events data...");

            List<Event> events = Arrays.asList(
                    createEvent(
                            "Mindfulness and Meditation Workshop",
                            "Join us for an interactive workshop on mindfulness techniques and guided meditation. Perfect for beginners and experienced practitioners alike.",
                            "mindfulness",
                            LocalDateTime.now().plusDays(7),
                            "Student Wellness Center, Room 201",
                            30,
                            2L
                    ),
                    createEvent(
                            "Campus Fitness Bootcamp",
                            "High-energy fitness bootcamp session designed for all fitness levels. Get active and meet fellow students!",
                            "fitness",
                            LocalDateTime.now().plusDays(14),
                            "Athletic Center",
                            25,
                            3L
                    ),
                    createEvent(
                            "Mental Health Awareness Week Kickoff",
                            "Launch event for Mental Health Awareness Week featuring guest speakers, resource booths, and wellness activities.",
                            "mental-health",
                            LocalDateTime.now().plusDays(3),
                            "Main Campus Atrium",
                            100,
                            7L
                    ),
                    createEvent(
                            "Healthy Cooking Workshop",
                            "Learn to prepare nutritious and budget-friendly meals perfect for student life. Ingredients and recipes provided.",
                            "nutrition",
                            LocalDateTime.now().plusDays(10),
                            "Culinary Arts Lab",
                            20,
                            4L
                    ),
                    createEvent(
                            "Stress Management Strategies Session",
                            "Practical session covering time management, study techniques, and coping strategies for academic stress.",
                            "stress-management",
                            LocalDateTime.now().plusDays(5),
                            "Library Conference Room",
                            40,
                            6L
                    ),
                    createEvent(
                            "Sleep Better Workshop",
                            "Evidence-based workshop on improving sleep quality with tips on sleep hygiene, routines, and environmental factors.",
                            "sleep",
                            LocalDateTime.now().plusDays(12),
                            "Student Services Building, Room 105",
                            35,
                            5L
                    ),
                    createEvent(
                            "Peer Support Group Meeting",
                            "Weekly peer support group where students can share experiences and support each other in a safe environment.",
                            "mental-health",
                            LocalDateTime.now().plusDays(2),
                            "Wellness Center Private Room",
                            15,
                            10L
                    ),
                    createEvent(
                            "Yoga for Students",
                            "Relaxing yoga session tailored for students. All levels welcome. Mats provided.",
                            "mindfulness",
                            LocalDateTime.now().plusDays(4),
                            "Athletic Center Studio B",
                            20,
                            8L
                    )
            );

            repository.saveAll(events);
            log.info("Successfully initialized {} events", events.size());
        } else {
            log.info("Events already exist, skipping initialization");
        }
    }

    private Event createEvent(String title, String description, String category,
                             LocalDateTime eventDate, String location, Integer capacity, Long resourceId) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setCategory(category);
        event.setEventDate(eventDate);
        event.setLocation(location);
        event.setCapacity(capacity);
        event.setResourceId(resourceId);
        return event;
    }
}
