package ca.gbc.eventservice.client;

import ca.gbc.eventservice.dto.EventResponse;
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

    @CircuitBreaker(name = "wellnessService", fallbackMethod = "getResourceByIdFallback")
    public EventResponse.WellnessResourceDto getResourceById(Long resourceId) {
        String url = wellnessResourceServiceUrl + "/api/resources/" + resourceId;
        log.info("Calling wellness-resource-service: {}", url);

        ResponseEntity<WellnessResourceResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                WellnessResourceResponse.class
        );

        if (response.getBody() != null) {
            return mapToDto(response.getBody());
        }
        return null;
    }

    public EventResponse.WellnessResourceDto getResourceByIdFallback(Long resourceId, Throwable throwable) {
        log.warn("Circuit breaker fallback triggered for wellness-resource-service. ResourceId: {}, Error: {}",
                resourceId, throwable.getMessage());
        return null;
    }

    @CircuitBreaker(name = "wellnessService", fallbackMethod = "getResourcesByCategoryFallback")
    public List<EventResponse.WellnessResourceDto> getResourcesByCategory(String category) {
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

    public List<EventResponse.WellnessResourceDto> getResourcesByCategoryFallback(String category, Throwable throwable) {
        log.warn("Circuit breaker fallback triggered for wellness-resource-service. Category: {}, Error: {}",
                category, throwable.getMessage());
        return Collections.emptyList();
    }

    private EventResponse.WellnessResourceDto mapToDto(WellnessResourceResponse response) {
        return new EventResponse.WellnessResourceDto(
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
