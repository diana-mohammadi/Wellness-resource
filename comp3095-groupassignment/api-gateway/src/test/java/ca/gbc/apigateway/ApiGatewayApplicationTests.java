package ca.gbc.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class ApiGatewayApplicationTests {

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // Disable Keycloak for tests
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> "http://localhost:8180/realms/wellness-hub");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> "http://localhost:8180/realms/wellness-hub/protocol/openid-connect/certs");
    }

    @Test
    void contextLoads() {
        // Basic context load test
    }
}
