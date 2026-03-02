package ca.gbc.goaltrackingservice.config;

import ca.gbc.goaltrackingservice.model.Goal;
import ca.gbc.goaltrackingservice.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final GoalRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            log.info("Initializing goals data...");

            List<Goal> goals = Arrays.asList(
                    createGoal(
                            "ST001",
                            "Daily Meditation Practice",
                            "Practice mindfulness meditation for 10 minutes every morning to reduce stress and improve focus",
                            "mindfulness",
                            LocalDate.now().plusMonths(1),
                            Goal.GoalStatus.ACTIVE,
                            25
                    ),
                    createGoal(
                            "ST001",
                            "Weekly Counseling Sessions",
                            "Attend professional counseling sessions every week to work through personal challenges",
                            "counseling",
                            LocalDate.now().plusMonths(3),
                            Goal.GoalStatus.ACTIVE,
                            40
                    ),
                    createGoal(
                            "ST002",
                            "Regular Exercise Routine",
                            "Exercise at the campus fitness center 3 times per week to improve physical and mental health",
                            "fitness",
                            LocalDate.now().plusMonths(2),
                            Goal.GoalStatus.ACTIVE,
                            60
                    ),
                    createGoal(
                            "ST002",
                            "Improve Sleep Schedule",
                            "Maintain a consistent sleep schedule, going to bed by 11 PM and waking up at 7 AM",
                            "sleep",
                            LocalDate.now().plusMonths(1),
                            Goal.GoalStatus.ACTIVE,
                            50
                    ),
                    createGoal(
                            "ST003",
                            "Healthy Eating Habits",
                            "Meet with nutritionist monthly and follow personalized meal plan for balanced diet",
                            "nutrition",
                            LocalDate.now().plusMonths(6),
                            Goal.GoalStatus.ACTIVE,
                            15
                    ),
                    createGoal(
                            "ST003",
                            "Stress Management Techniques",
                            "Learn and practice stress management techniques from weekly workshops",
                            "stress-management",
                            LocalDate.now().plusMonths(2),
                            Goal.GoalStatus.ACTIVE,
                            30
                    ),
                    createGoal(
                            "ST004",
                            "Mental Health Awareness",
                            "Attend mental health awareness sessions and learn about resources available on campus",
                            "mental-health",
                            LocalDate.now().plusMonths(1),
                            Goal.GoalStatus.COMPLETED,
                            100
                    )
            );

            repository.saveAll(goals);
            log.info("Successfully initialized {} goals", goals.size());
        } else {
            log.info("Goals already exist, skipping initialization");
        }
    }

    private Goal createGoal(String studentId, String title, String description, String category,
                           LocalDate targetDate, Goal.GoalStatus status, Integer progress) {
        Goal goal = new Goal();
        goal.setStudentId(studentId);
        goal.setTitle(title);
        goal.setDescription(description);
        goal.setCategory(category);
        goal.setTargetDate(targetDate);
        goal.setStatus(status);
        goal.setProgress(progress);
        return goal;
    }
}
