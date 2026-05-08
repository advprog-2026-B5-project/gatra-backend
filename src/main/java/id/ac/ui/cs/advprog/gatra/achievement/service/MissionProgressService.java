package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MissionProgressResponse;

import java.util.List;
import java.util.UUID;

public interface MissionProgressService {
    List<MissionProgressResponse> getActiveMissionsWithProgress(String username);
    List<MissionProgressResponse> incrementProgress(UUID userId, String actionType);
    MissionProgressResponse claimReward(String username, UUID missionId);
}