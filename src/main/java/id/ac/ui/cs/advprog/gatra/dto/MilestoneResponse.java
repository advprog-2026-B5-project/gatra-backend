package id.ac.ui.cs.advprog.gatra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneResponse {
    private String actionType;
    private Integer newTotalCount;
    private List<AchievementResponse> newlyUnlockedAchievements;
    private List<MissionProgressResponse> completedMissions;
}
