package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;

import java.util.List;
import java.util.UUID;

public interface AchievementService {
    List<AchievementResponse> getAllAchievements();
    AchievementResponse getAchievementById(UUID id);
    AchievementResponse createAchievement(AchievementRequest request);
    AchievementResponse updateAchievement(UUID id, AchievementRequest request);
    void deleteAchievement(UUID id);
}