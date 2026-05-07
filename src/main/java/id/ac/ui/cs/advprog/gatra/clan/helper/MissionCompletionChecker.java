package id.ac.ui.cs.advprog.gatra.clan.helper;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserMissionProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class MissionCompletionChecker {

    private final UserMissionProgressRepository missionProgressRepository;

    public boolean hasCompletedAnyMission(String userId) {
        UUID userUUID = parseUserId(userId);
        return missionProgressRepository
                .findByUserId(userUUID)
                .stream()
                .anyMatch(UserMissionProgress::getIsClaimed);
    }

    private UUID parseUserId(String userId) {
        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid userId format: " + userId, e);
        }
    }
}