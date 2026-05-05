package id.ac.ui.cs.advprog.gatra.achievement.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class DailyMissionResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer targetCount;
    private Integer rewardPoints;
    private String actionType;
    private String status;
}