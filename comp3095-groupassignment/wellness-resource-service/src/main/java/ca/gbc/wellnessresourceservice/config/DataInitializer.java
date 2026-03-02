package ca.gbc.wellnessresourceservice.config;

import ca.gbc.wellnessresourceservice.model.WellnessResource;
import ca.gbc.wellnessresourceservice.repository.WellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final WellnessResourceRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            log.info("Initializing wellness resources data...");

            List<WellnessResource> resources = Arrays.asList(
                    createResource(
                            "GBC Counseling Services",
                            "Professional counseling services available to all George Brown College students. Free and confidential support for mental health, stress, and personal issues.",
                            "counseling",
                            "https://www.georgebrown.ca/student-life/wellness"
                    ),
                    createResource(
                            "Mindfulness Meditation Guide",
                            "A comprehensive guide to mindfulness meditation techniques for beginners. Learn how to reduce stress and improve focus through daily meditation practices.",
                            "mindfulness",
                            "https://www.mindful.org/meditation/mindfulness-getting-started"
                    ),
                    createResource(
                            "Campus Fitness Center",
                            "Access to state-of-the-art fitness facilities including gym equipment, group classes, and personal training services for GBC students.",
                            "fitness",
                            "https://www.georgebrown.ca/campus-life/athletics"
                    ),
                    createResource(
                            "Nutrition Consultation Services",
                            "One-on-one consultations with registered dietitians to help you develop healthy eating habits and meal plans tailored to student life.",
                            "nutrition",
                            "https://www.georgebrown.ca/student-life/health-services"
                    ),
                    createResource(
                            "Sleep Hygiene Tips",
                            "Evidence-based strategies to improve sleep quality. Learn about sleep schedules, bedroom environment, and pre-sleep routines for better rest.",
                            "sleep",
                            "https://www.sleepfoundation.org/sleep-hygiene"
                    ),
                    createResource(
                            "Stress Management Workshop",
                            "Interactive workshops teaching practical stress management techniques including time management, relaxation exercises, and coping strategies.",
                            "stress-management",
                            "https://www.georgebrown.ca/student-life/workshops"
                    ),
                    createResource(
                            "Mental Health Crisis Support",
                            "24/7 crisis support hotline and resources for students experiencing mental health emergencies. Confidential and immediate assistance available.",
                            "mental-health",
                            "https://www.crisisservicescanada.ca"
                    ),
                    createResource(
                            "Yoga and Wellness Classes",
                            "Weekly yoga sessions designed specifically for students. Improve flexibility, reduce stress, and connect with other students in a supportive environment.",
                            "mindfulness",
                            "https://www.georgebrown.ca/campus-life/wellness-programs"
                    ),
                    createResource(
                            "Study Skills and Time Management",
                            "Workshop series focused on developing effective study habits, time management skills, and work-life balance strategies for academic success.",
                            "workshops",
                            "https://www.georgebrown.ca/academic-support"
                    ),
                    createResource(
                            "Peer Support Program",
                            "Connect with trained peer supporters who understand student challenges. Share experiences and get support from fellow students.",
                            "mental-health",
                            "https://www.georgebrown.ca/student-life/peer-support"
                    )
            );

            repository.saveAll(resources);
            log.info("Successfully initialized {} wellness resources", resources.size());
        } else {
            log.info("Wellness resources already exist, skipping initialization");
        }
    }

    private WellnessResource createResource(String title, String description, String category, String url) {
        WellnessResource resource = new WellnessResource();
        resource.setTitle(title);
        resource.setDescription(description);
        resource.setCategory(category);
        resource.setUrl(url);
        return resource;
    }
}
