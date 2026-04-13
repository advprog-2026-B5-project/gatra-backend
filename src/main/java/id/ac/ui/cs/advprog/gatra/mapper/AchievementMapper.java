package id.ac.ui.cs.advprog.gatra.mapper;

import id.ac.ui.cs.advprog.gatra.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.model.Achievement;
import org.springframework.stereotype.Component;

@Component
public class AchievementMapper {
    public AchievementResponse toResponse(Achievement achievement) {
        return AchievementResponse.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .category(achievement.getCategory())
                .milestoneThreshold(achievement.getMilestoneThreshold())
                .description(achievement.getDescription())
                .badgeUrl(achievement.getBadgeUrl())
                .build();
    }
}