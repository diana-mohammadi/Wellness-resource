package ca.gbc.eventservice.event;

import ca.gbc.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventConsumer {

    private final EventService eventService;

    @KafkaListener(topics = "goal-completed", groupId = "event-service-group")
    public void consumeGoalCompletedEvent(GoalCompletedEvent event) {
        log.info("Received GoalCompletedEvent for goal: {} by student: {} in category: {}",
                event.getGoalId(), event.getStudentId(), event.getCategory());

        // Recommend relevant wellness events based on the completed goal's category
        try {
            eventService.recommendEventsForCompletedGoal(event.getStudentId(), event.getCategory());
            log.info("Successfully processed event recommendations for student: {}", event.getStudentId());
        } catch (Exception e) {
            log.error("Error processing GoalCompletedEvent for goal: {}", event.getGoalId(), e);
        }
    }
}
