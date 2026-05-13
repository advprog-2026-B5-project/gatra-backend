package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.model.StudentMilestoneProgress;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.achievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.StudentMilestoneProgressRepository;
import id.ac.ui.cs.advprog.gatra.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MilestoneServiceImpl implements MilestoneService {

    private final StudentMilestoneProgressRepository milestoneProgressRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementService userAchievementService;
    private final UserService userService;
    private final AchievementMapper achievementMapper;

    @Override
    @Transactional
    public MilestoneResponse recordAction(UUID userId, ActionType actionType) {
        User user = userService.getUserEntityById(userId);

        StudentMilestoneProgress progress = milestoneProgressRepository
                .findByUserIdAndActionType(userId, actionType)
                .orElseGet(() -> StudentMilestoneProgress.builder()
                        .user(user)
                        .actionType(actionType)
                        .totalCount(0)
                        .build());

        progress.setTotalCount(progress.getTotalCount() + 1);
        milestoneProgressRepository.save(progress);

        int newCount = progress.getTotalCount();

        List<Achievement> matchingAchievements =
                achievementRepository.findByCategoryAndMilestoneThresholdLessThanEqual(
                        actionType, newCount);

        List<AchievementResponse> newlyUnlocked = new ArrayList<>();

        for (Achievement achievement : matchingAchievements) {
            boolean newlyUnlockedNow = userAchievementService.unlockIfNotYet(userId, achievement);

            if (newlyUnlockedNow) {
                newlyUnlocked.add(achievementMapper.toResponse(achievement));
            }
        }

        return MilestoneResponse.builder()
                .actionType(actionType.name())
                .newTotalCount(newCount)
                .newlyUnlockedAchievements(newlyUnlocked)
                .build();
    }
}