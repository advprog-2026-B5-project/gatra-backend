package id.ac.ui.cs.advprog.gatra.mapper;

import id.ac.ui.cs.advprog.gatra.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.model.Achievement;
import id.ac.ui.cs.advprog.gatra.model.UserAchievement;
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

    public AchievementResponse toResponseFromUserAchievement(UserAchievement userAchievement) {
        AchievementResponse response = toResponse(userAchievement.getAchievement());
        response.setUnlockedAt(userAchievement.getUnlockedAt().toString());
        response.setDisplayed(userAchievement.isDisplayed());
        return response;
    }

    public Achievement toEntity(AchievementRequest request) {
        return Achievement.builder()
                .name(request.getName())
                .category(request.getCategory())
                .milestoneThreshold(request.getMilestoneThreshold())
                .description(request.getDescription())
                .badgeUrl(request.getBadgeUrl())
                .build();
    }

    public void updateEntity(Achievement achievement, AchievementRequest request) {
        achievement.setName(request.getName());
        achievement.setCategory(request.getCategory());
        achievement.setMilestoneThreshold(request.getMilestoneThreshold());
        achievement.setDescription(request.getDescription());
        achievement.setBadgeUrl(request.getBadgeUrl());
    }
}