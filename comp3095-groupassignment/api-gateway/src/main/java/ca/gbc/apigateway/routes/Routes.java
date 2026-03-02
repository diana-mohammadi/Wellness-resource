package ca.gbc.apigateway.routes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
@Slf4j
public class Routes {

    @Value("${wellness.service.url:http://wellness-resource-service:8081}")
    private String wellnessServiceUrl;

    @Value("${goal.service.url:http://goal-tracking-service:8082}")
    private String goalServiceUrl;

    @Value("${event.service.url:http://event-service:8083}")
    private String eventServiceUrl;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Wellness Resource Service Routes
                .route("wellness-service", r -> r
                        .path("/api/resources/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("wellnessServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/wellness")))
                        .uri(wellnessServiceUrl))

                // Wellness Service Swagger
                .route("wellness-service-swagger", r -> r
                        .path("/aggregate/wellness-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/wellness-service/v3/api-docs", "/v3/api-docs"))
                        .uri(wellnessServiceUrl))

                // Goal Tracking Service Routes
                .route("goal-service", r -> r
                        .path("/api/goals/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("goalServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/goals")))
                        .uri(goalServiceUrl))

                // Goal Service Swagger
                .route("goal-service-swagger", r -> r
                        .path("/aggregate/goal-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/goal-service/v3/api-docs", "/v3/api-docs"))
                        .uri(goalServiceUrl))

                // Event Service Routes
                .route("event-service", r -> r
                        .path("/api/events/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("eventServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/events")))
                        .uri(eventServiceUrl))

                // Event Service Swagger
                .route("event-service-swagger", r -> r
                        .path("/aggregate/event-service/v3/api-docs")
                        .filters(f -> f.rewritePath("/aggregate/event-service/v3/api-docs", "/v3/api-docs"))
                        .uri(eventServiceUrl))

                .build();
    }
}
