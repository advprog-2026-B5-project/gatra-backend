package id.ac.ui.cs.advprog.gatra.achievement.event;

import java.util.UUID;

public record MissionRewardClaimedEvent(
        UUID userId,
        UUID missionId,
        int rewardPoints
) {
}
