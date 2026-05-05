package id.ac.ui.cs.advprog.gatra.achievement.dto;

import id.ac.ui.cs.advprog.gatra.model.ActionType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AchievementResponse {
    private UUID id;
    private String name;
    private ActionType category;
    private Integer milestoneThreshold;
    private String description;
    private String badgeUrl;
    private String unlockedAt;
    private boolean isDisplayed;
}