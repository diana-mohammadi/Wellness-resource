package ca.gbc.wellnessresourceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WellnessResourceResponse implements Serializable {

    private Long resourceId;
    private String title;
    private String description;
    private String category;
    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
