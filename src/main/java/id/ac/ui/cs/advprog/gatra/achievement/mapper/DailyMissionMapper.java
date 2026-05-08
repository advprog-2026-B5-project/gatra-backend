package id.ac.ui.cs.advprog.gatra.achievement.mapper;

import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;

import org.springframework.stereotype.Component;

@Component
public class DailyMissionMapper {

    public DailyMissionResponse toResponse(DailyMission mission) {
        if (mission == null) return null;

        return DailyMissionResponse.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .targetCount(mission.getTargetCount())
                .rewardPoints(mission.getRewardPoints())
                .actionType(mission.getActionType() != null ? mission.getActionType().name() : null)
                .status(mission.getStatus() != null ? mission.getStatus().name() : null)
                .build();
    }

    public DailyMission toEntity(DailyMissionRequest request) {
        if (request == null) return null;

        return DailyMission.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .targetCount(request.getTargetCount())
                .rewardPoints(request.getRewardPoints())
                .actionType(ActionType.valueOf(request.getActionType()))
                .status(MissionStatus.valueOf(request.getStatus()))
                .build();
    }

    public void updateEntityFromRequest(DailyMissionRequest request, DailyMission mission) {
        if (request == null || mission == null) return;

        mission.setTitle(request.getTitle());
        mission.setDescription(request.getDescription());
        mission.setTargetCount(request.getTargetCount());
        mission.setRewardPoints(request.getRewardPoints());
        
        if (request.getActionType() != null) {
            mission.setActionType(ActionType.valueOf(request.getActionType()));
        }
        
        if (request.getStatus() != null) {
            mission.setStatus(MissionStatus.valueOf(request.getStatus()));
        }
    }
}