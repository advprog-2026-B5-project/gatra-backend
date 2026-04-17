package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.model.*;
import id.ac.ui.cs.advprog.gatra.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.repository.StudentMilestoneProgressRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MilestoneServiceImpl implements MilestoneService {

    private final StudentMilestoneProgressRepository milestoneProgressRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;
    private final AchievementMapper achievementMapper;

    @Override
    @Transactional
    public MilestoneResponse recordAction(UUID userId, ActionType actionType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

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
        log.info("User {} performed action {} — total count now: {}",
                userId, actionType, newCount);

        List<Achievement> matchingAchievements =
                achievementRepository.findByCategoryAndMilestoneThresholdLessThanEqual(
                        actionType, newCount);

        List<AchievementResponse> newlyUnlocked = new ArrayList<>();

        for (Achievement achievement : matchingAchievements) {
            boolean alreadyUnlocked = userAchievementRepository
                    .existsByUserIdAndAchievementId(userId, achievement.getId());

            if (!alreadyUnlocked) {
                UserAchievement userAchievement = UserAchievement.builder()
                        .user(user)
                        .achievement(achievement)
                        .isDisplayed(false)
                        .build();
                userAchievementRepository.save(userAchievement);

                AchievementResponse response = achievementMapper.toResponseFromUserAchievement(userAchievement);
                newlyUnlocked.add(response);

                log.info("User {} unlocked achievement: {} (threshold: {})",
                        userId, achievement.getName(), achievement.getMilestoneThreshold());
            }
        }

        return MilestoneResponse.builder()
                .actionType(actionType.name())
                .newTotalCount(newCount)
                .newlyUnlockedAchievements(newlyUnlocked)
                .build();
    }
}
