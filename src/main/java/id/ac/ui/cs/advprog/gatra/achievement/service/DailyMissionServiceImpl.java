package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.DailyMissionMapper;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.achievement.repository.DailyMissionRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DailyMissionServiceImpl implements DailyMissionService {

    private final DailyMissionRepository missionRepository;
    private final DailyMissionMapper missionMapper;

    @Override
    public DailyMissionResponse createMission(DailyMissionRequest request) {
        DailyMission mission = missionMapper.toEntity(request);
        return missionMapper.toResponse(missionRepository.save(mission));
    }

    @Override
    public List<DailyMissionResponse> getAllMissions() {
        return missionRepository.findAll().stream()
                .map(missionMapper::toResponse)
                .toList();
    }

    @Override
    public DailyMissionResponse getMissionById(UUID id) {
        return missionMapper.toResponse(findMissionByIdOrThrow(id));
    }

    @Override
    public DailyMissionResponse updateMission(UUID id, DailyMissionRequest request) {
        DailyMission mission = findMissionByIdOrThrow(id);
        missionMapper.updateEntityFromRequest(request, mission);
        return missionMapper.toResponse(missionRepository.save(mission));
    }

    @Override
    public void deleteMission(UUID id) {
        missionRepository.delete(findMissionByIdOrThrow(id));
    }

    @Override
    public List<DailyMissionResponse> getActiveMissions() {
        return missionRepository.findByStatus(MissionStatus.ACTIVE).stream()
                .map(missionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void rotateMissions() {
        List<DailyMission> allMissions = missionRepository.findAll();
        allMissions.forEach(m -> m.setStatus(MissionStatus.INACTIVE));

        java.util.Collections.shuffle(allMissions);
        allMissions.stream()
            .limit(3)
            .forEach(m -> m.setStatus(MissionStatus.ACTIVE));

        missionRepository.saveAll(allMissions);
    }

    private DailyMission findMissionByIdOrThrow(UUID id) {
        return missionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("DailyMission", id));
    }
}