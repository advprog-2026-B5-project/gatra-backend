package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.model.ActionType;
import id.ac.ui.cs.advprog.gatra.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.repository.DailyMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyMissionServiceImpl implements DailyMissionService {

    private final DailyMissionRepository missionRepository;

    @Override
    public DailyMissionResponse createMission(DailyMissionRequest request) {
        DailyMission mission = DailyMission.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .targetCount(request.getTargetCount())
                .rewardPoints(request.getRewardPoints())
                .actionType(ActionType.valueOf(request.getActionType()))
                .status(MissionStatus.valueOf(request.getStatus()))
                .build();

        return mapToResponse(missionRepository.save(mission));
    }

    @Override
    public List<DailyMissionResponse> getAllMissions() {
        return missionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DailyMissionResponse getMissionById(UUID id) {
        DailyMission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DailyMission", id));
        return mapToResponse(mission);
    }

    @Override
    public DailyMissionResponse updateMission(UUID id, DailyMissionRequest request) {
        DailyMission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DailyMission", id));

        mission.setTitle(request.getTitle());
        mission.setDescription(request.getDescription());
        mission.setTargetCount(request.getTargetCount());
        mission.setRewardPoints(request.getRewardPoints());
        mission.setActionType(ActionType.valueOf(request.getActionType()));
        mission.setStatus(MissionStatus.valueOf(request.getStatus()));

        return mapToResponse(missionRepository.save(mission));
    }

    @Override
    public void deleteMission(UUID id) {
        DailyMission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DailyMission", id));
        missionRepository.delete(mission);
    }

    private DailyMissionResponse mapToResponse(DailyMission mission) {
        return DailyMissionResponse.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .targetCount(mission.getTargetCount())
                .rewardPoints(mission.getRewardPoints())
                .actionType(mission.getActionType().name())
                .status(mission.getStatus().name())
                .build();
    }
}