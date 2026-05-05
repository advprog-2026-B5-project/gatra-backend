package id.ac.ui.cs.advprog.gatra.achievement.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
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