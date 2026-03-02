package ca.gbc.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private Long eventId;
    private String title;
    private String description;
    private String category;
    private LocalDateTime eventDate;
    private String location;
    private Integer capacity;
    private Long resourceId;
    private WellnessResourceDto linkedResource;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WellnessResourceDto {
        private Long resourceId;
        private String title;
        private String description;
        private String category;
        private String url;
    }
}
