package ca.gbc.wellnessresourceservice;

import ca.gbc.wellnessresourceservice.dto.WellnessResourceRequest;
import ca.gbc.wellnessresourceservice.dto.WellnessResourceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class WellnessResourceServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private WellnessResourceRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new WellnessResourceRequest();
        testRequest.setTitle("Test Resource");
        testRequest.setDescription("Test Description");
        testRequest.setCategory("counseling");
        testRequest.setUrl("https://test.com");
    }

    @Test
    void shouldCreateResource() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Resource"))
                .andExpect(jsonPath("$.category").value("counseling"));
    }

    @Test
    void shouldGetAllResources() throws Exception {
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetResourceById() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        WellnessResourceResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                WellnessResourceResponse.class
        );

        mockMvc.perform(get("/api/resources/" + response.getResourceId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceId").value(response.getResourceId()))
                .andExpect(jsonPath("$.title").value("Test Resource"));
    }

    @Test
    void shouldGetResourcesByCategory() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/resources").param("category", "counseling"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].category", everyItem(is("counseling"))));
    }

    @Test
    void shouldSearchResources() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/resources/search?keyword=Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldUpdateResource() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        WellnessResourceResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                WellnessResourceResponse.class
        );

        testRequest.setTitle("Updated Resource");

        mockMvc.perform(put("/api/resources/" + created.getResourceId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Resource"));
    }

    @Test
    void shouldDeleteResource() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        WellnessResourceResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                WellnessResourceResponse.class
        );

        mockMvc.perform(delete("/api/resources/" + created.getResourceId()))
                .andExpect(status().isNoContent());
    }
}
