package ca.gbc.goaltrackingservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalEventProducer {

    private final KafkaTemplate<String, GoalCompletedEvent> kafkaTemplate;

    private static final String TOPIC = "goal-completed";

    public void sendGoalCompletedEvent(GoalCompletedEvent event) {
        log.info("Sending GoalCompletedEvent for goal: {} to topic: {}", event.getGoalId(), TOPIC);

        CompletableFuture<SendResult<String, GoalCompletedEvent>> future =
                kafkaTemplate.send(TOPIC, event.getGoalId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent GoalCompletedEvent for goal: {} with offset: {}",
                        event.getGoalId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send GoalCompletedEvent for goal: {}", event.getGoalId(), ex);
            }
        });
    }
}
