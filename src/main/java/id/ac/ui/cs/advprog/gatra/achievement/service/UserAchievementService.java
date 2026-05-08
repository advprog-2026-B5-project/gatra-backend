package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;

import java.util.List;
import java.util.UUID;

public interface UserAchievementService {
    List<AchievementResponse> getMyAchievements(String username);
    List<AchievementResponse> getDisplayedAchievements(String username);
    void toggleDisplayAchievement(String username, UUID achievementId, boolean displayed);
    boolean unlockIfNotYet(UUID userId, Achievement achievement);
}