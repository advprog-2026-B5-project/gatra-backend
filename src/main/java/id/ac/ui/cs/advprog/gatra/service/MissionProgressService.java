package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.MissionProgressResponse;

import java.util.List;
import java.util.UUID;

public interface MissionProgressService {
    List<MissionProgressResponse> getActiveMissionsWithProgress(UUID userId);
    List<MissionProgressResponse> incrementProgress(UUID userId, String actionType);
    MissionProgressResponse claimReward(UUID userId, UUID missionId);
}