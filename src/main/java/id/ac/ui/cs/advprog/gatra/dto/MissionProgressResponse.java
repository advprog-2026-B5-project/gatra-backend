package id.ac.ui.cs.advprog.gatra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionProgressResponse {
    private UUID missionId;
    private String title;
    private String description;
    private String actionType;
    private Integer targetCount;
    private Integer rewardPoints;
    private String status;

    private Integer currentCount;
    private Boolean isClaimed;
    private Boolean isCompleted;
}