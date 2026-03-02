package ca.gbc.goaltrackingservice;

import ca.gbc.goaltrackingservice.dto.GoalRequest;
import ca.gbc.goaltrackingservice.dto.GoalResponse;
import ca.gbc.goaltrackingservice.dto.ProgressUpdateRequest;
import ca.gbc.goaltrackingservice.model.Goal;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class GoalTrackingServiceIntegrationTest {

    @Container
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private GoalRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new GoalRequest();
        testRequest.setStudentId("TEST001");
        testRequest.setTitle("Test Goal");
        testRequest.setDescription("Test Description");
        testRequest.setCategory("mindfulness");
        testRequest.setTargetDate(LocalDate.now().plusMonths(1));
        testRequest.setStatus(Goal.GoalStatus.ACTIVE);
        testRequest.setProgress(0);
    }

    @Test
    void shouldCreateGoal() throws Exception {
        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Goal"))
                .andExpect(jsonPath("$.studentId").value("TEST001"));
    }

    @Test
    void shouldGetAllGoals() throws Exception {
        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetGoalById() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        GoalResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                GoalResponse.class
        );

        mockMvc.perform(get("/api/goals/" + response.getGoalId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalId").value(response.getGoalId()));
    }

    @Test
    void shouldGetGoalsByStudent() throws Exception {
        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/goals/student/TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].studentId", everyItem(is("TEST001"))));
    }

    @Test
    void shouldUpdateGoal() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        GoalResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                GoalResponse.class
        );

        testRequest.setTitle("Updated Goal");

        mockMvc.perform(put("/api/goals/" + created.getGoalId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Goal"));
    }

    @Test
    void shouldUpdateProgress() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        GoalResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                GoalResponse.class
        );

        ProgressUpdateRequest progressUpdate = new ProgressUpdateRequest();
        progressUpdate.setProgress(50);

        mockMvc.perform(put("/api/goals/" + created.getGoalId() + "/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(progressUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(50));
    }

    @Test
    void shouldDeleteGoal() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        GoalResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                GoalResponse.class
        );

        mockMvc.perform(delete("/api/goals/" + created.getGoalId()))
                .andExpect(status().isNoContent());
    }
}
