package id.ac.ui.cs.advprog.gatra.achievement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;

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
    @JsonProperty("isDisplayed")
    private boolean isDisplayed;
}