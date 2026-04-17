package id.ac.ui.cs.advprog.gatra.mapper;

import id.ac.ui.cs.advprog.gatra.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.model.UserMissionProgress;
import org.springframework.stereotype.Component;

@Component
public class MissionProgressMapper {

    public MissionProgressResponse toResponse(DailyMission mission, UserMissionProgress progress) {
        int currentCount = (progress != null) ? progress.getCurrentCount() : 0;
        boolean isClaimed = (progress != null) && Boolean.TRUE.equals(progress.getIsClaimed());

        return MissionProgressResponse.builder()
                .missionId(mission.getId())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .actionType(mission.getActionType() != null ? mission.getActionType().name() : null)
                .targetCount(mission.getTargetCount())
                .rewardPoints(mission.getRewardPoints())
                .status(mission.getStatus() != null ? mission.getStatus().name() : null)
                .currentCount(currentCount)
                .isClaimed(isClaimed)
                .isCompleted(currentCount >= mission.getTargetCount())
                .build();
    }
}