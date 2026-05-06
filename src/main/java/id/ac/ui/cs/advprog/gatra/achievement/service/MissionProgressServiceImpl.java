package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.MissionProgressMapper;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.gatra.achievement.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserMissionProgressRepository;
import id.ac.ui.cs.advprog.gatra.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionProgressServiceImpl implements MissionProgressService {

    private final DailyMissionRepository dailyMissionRepository;
    private final UserMissionProgressRepository progressRepository;
    private final UserService userService;
    private final MissionProgressMapper progressMapper;

    @Override
    public List<MissionProgressResponse> getActiveMissionsWithProgress(String username) {
        User user = userService.getUserEntityByUsername(username);
        UUID userId = user.getId();

        List<DailyMission> activeMissions = dailyMissionRepository.findByStatus(MissionStatus.ACTIVE);

        return activeMissions.stream()
                .map(mission -> {
                    UserMissionProgress progress = progressRepository
                            .findByUserIdAndMissionId(userId, mission.getId())
                            .orElse(null);
                    return progressMapper.toResponse(mission, progress);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<MissionProgressResponse> incrementProgress(UUID userId, String actionType) {
        User user = userService.getUserEntityById(userId);

        ActionType type = ActionType.valueOf(actionType);

        List<DailyMission> matchingMissions = dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)
                .stream()
                .filter(m -> m.getActionType() == type)
                .collect(Collectors.toList());

        List<MissionProgressResponse> newlyCompleted = new ArrayList<>();

        for (DailyMission mission : matchingMissions) {
            UserMissionProgress progress = progressRepository
                    .findByUserIdAndMissionId(userId, mission.getId())
                    .orElseGet(() -> UserMissionProgress.builder()
                            .user(user)
                            .mission(mission)
                            .currentCount(0)
                            .isClaimed(false)
                            .build());

            if (progress.getCurrentCount() < mission.getTargetCount()) {
                progress.setCurrentCount(progress.getCurrentCount() + 1);
                progressRepository.save(progress);

                if (progress.getCurrentCount().equals(mission.getTargetCount())) {
                    newlyCompleted.add(progressMapper.toResponse(mission, progress));
                }
            }
        }

        return newlyCompleted;
    }

    @Override
    @Transactional
    public MissionProgressResponse claimReward(String username, UUID missionId) {
        User user = userService.getUserEntityByUsername(username);
        
        UUID userId = user.getId();

        DailyMission mission = dailyMissionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("DailyMission", missionId));

        UserMissionProgress progress = progressRepository
                .findByUserIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new IllegalArgumentException("Belum ada progres untuk misi ini."));

        if (progress.getCurrentCount() < mission.getTargetCount()) {
            throw new IllegalArgumentException("Misi belum selesai.");
        }
        if (Boolean.TRUE.equals(progress.getIsClaimed())) {
            throw new IllegalArgumentException("Reward sudah diklaim.");
        }

        progress.setIsClaimed(true);
        progressRepository.save(progress);

        return progressMapper.toResponse(mission, progress);
    }
}