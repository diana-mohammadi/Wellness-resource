package ca.gbc.apigateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/wellness")
    public Mono<ResponseEntity<Map<String, Object>>> wellnessFallback() {
        log.warn("Wellness service is unavailable - returning fallback response");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", "Wellness Resource Service is currently unavailable. Please try again later.");
        response.put("service", "wellness-resource-service");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/goals")
    public Mono<ResponseEntity<Map<String, Object>>> goalsFallback() {
        log.warn("Goal tracking service is unavailable - returning fallback response");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", "Goal Tracking Service is currently unavailable. Please try again later.");
        response.put("service", "goal-tracking-service");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/events")
    public Mono<ResponseEntity<Map<String, Object>>> eventsFallback() {
        log.warn("Event service is unavailable - returning fallback response");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", "Event Service is currently unavailable. Please try again later.");
        response.put("service", "event-service");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}
