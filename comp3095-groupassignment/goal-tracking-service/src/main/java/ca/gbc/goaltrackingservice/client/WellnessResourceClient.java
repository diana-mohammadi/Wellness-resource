package ca.gbc.goaltrackingservice.client;

import ca.gbc.goaltrackingservice.dto.GoalResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WellnessResourceClient {

    private final RestTemplate restTemplate;

    @Value("${wellness.resource.service.url}")
    private String wellnessResourceServiceUrl;

    @CircuitBreaker(name = "wellnessService", fallbackMethod = "getResourcesByCategoryFallback")
    public List<GoalResponse.WellnessResourceDto> getResourcesByCategory(String category) {
        String url = wellnessResourceServiceUrl + "/api/resources?category=" + category;
        log.info("Calling wellness-resource-service: {}", url);

        ResponseEntity<WellnessResourceResponse[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                WellnessResourceResponse[].class
        );

        if (response.getBody() != null) {
            return java.util.Arrays.stream(response.getBody())
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<GoalResponse.WellnessResourceDto> getResourcesByCategoryFallback(String category, Throwable throwable) {
        log.warn("Circuit breaker fallback triggered for wellness-resource-service. Category: {}, Error: {}",
                category, throwable.getMessage());
        return Collections.emptyList();
    }

    private GoalResponse.WellnessResourceDto mapToDto(WellnessResourceResponse response) {
        return new GoalResponse.WellnessResourceDto(
                response.getResourceId(),
                response.getTitle(),
                response.getDescription(),
                response.getCategory(),
                response.getUrl()
        );
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class WellnessResourceResponse {
        private Long resourceId;
        private String title;
        private String description;
        private String category;
        private String url;
    }
}
