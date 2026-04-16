package id.ac.ui.cs.advprog.gatra.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MilestoneResponse {
    private String actionType;
    private Integer newTotalCount;
    private List<AchievementResponse> newlyUnlockedAchievements;
}
