package ca.gbc.apigateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${keycloak.auth-server-url:http://keycloak:8080}")
    private String keycloakUrl;

    @Value("${keycloak.realm:wellness-hub}")
    private String realm;

    @Bean
    public OpenAPI customOpenAPI() {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        String authUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/auth";

        return new OpenAPI()
                .info(new Info()
                        .title("Student Wellness Hub API")
                        .version("2.0.0")
                        .description("API Gateway for the Student Wellness Hub microservices platform. " +
                                "This API provides access to wellness resources, goal tracking, and event management.")
                        .contact(new Contact()
                                .name("GBC Wellness Hub Team")
                                .email("wellness@georgebrown.ca")))
                .addSecurityItem(new SecurityRequirement().addList("oauth2"))
                .components(new Components()
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .password(new OAuthFlow()
                                                .tokenUrl(tokenUrl))
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(authUrl)
                                                .tokenUrl(tokenUrl)))));
    }
}
