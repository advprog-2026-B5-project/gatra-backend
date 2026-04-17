package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.model.ActionType;
import id.ac.ui.cs.advprog.gatra.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.repository.DailyMissionRepository;
import jakarta.transaction.Transactional;
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

    @Override
    public List<DailyMissionResponse> getActiveMissions() {
        return missionRepository.findByStatus(MissionStatus.ACTIVE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void rotateMissions() {
        List<DailyMission> allMissions = missionRepository.findAll();

        // Reset semua misi menjadi INACTIVE
        allMissions.forEach(m -> m.setStatus(MissionStatus.INACTIVE));

        // Acak urutan misi
        java.util.Collections.shuffle(allMissions);

        // Pilih maksimal 3 misi teratas untuk diaktifkan
        int countToActivate = Math.min(3, allMissions.size());
        for (int i = 0; i < countToActivate; i++) {
            allMissions.get(i).setStatus(MissionStatus.ACTIVE);
        }

        // Simpan perubahan ke database
        missionRepository.saveAll(allMissions);
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