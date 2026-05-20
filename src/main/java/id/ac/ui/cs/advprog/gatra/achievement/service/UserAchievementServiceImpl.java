package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.auth.service.UserService;
import id.ac.ui.cs.advprog.gatra.achievement.strategy.DisplayAchievementStrategy;
import id.ac.ui.cs.advprog.gatra.achievement.strategy.HideAchievementStrategy;
import id.ac.ui.cs.advprog.gatra.achievement.strategy.ShowAchievementStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAchievementServiceImpl implements UserAchievementService {

    private static final int MAX_DISPLAYED_ACHIEVEMENTS = 3;

    private final UserAchievementRepository userAchievementRepository;
    private final UserService userService;
    private final AchievementMapper achievementMapper;

    private final ShowAchievementStrategy showStrategy;
    private final HideAchievementStrategy hideStrategy;

    @Override
    public List<AchievementResponse> getMyAchievements(String username) {
        UUID userId = userService.getUserEntityByUsername(username).getId();
        return userAchievementRepository.findByUserId(userId).stream()
                .map(achievementMapper::toResponseFromUserAchievement)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getDisplayedAchievements(String username) {
        UUID userId = userService.getUserEntityByUsername(username).getId();
        return userAchievementRepository.findByUserIdAndIsDisplayedTrue(userId).stream()
                .map(achievementMapper::toResponseFromUserAchievement)
                .limit(MAX_DISPLAYED_ACHIEVEMENTS)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleDisplayAchievement(String username, UUID achievementId, boolean displayed) {
        UserAchievement userAchievement = findUserAchievementOrThrow(username, achievementId);

        DisplayAchievementStrategy strategy = displayed ? showStrategy : hideStrategy;

        strategy.execute(userAchievement, userAchievementRepository);
    }

    private UserAchievement findUserAchievementOrThrow(String username, UUID achievementId) {
        UUID userId = userService.getUserEntityByUsername(username).getId();
        return userAchievementRepository
                .findByUserIdAndAchievementId(userId, achievementId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAchievement", achievementId));
    }

    @Override
    @Transactional
    public boolean unlockIfNotYet(UUID userId, Achievement achievement) {
        boolean alreadyUnlocked = userAchievementRepository
                .existsByUserIdAndAchievementId(userId, achievement.getId());

        if (alreadyUnlocked) return false;

        // SECURE FIX: We only save the UUID now, completely decoupling the User entity!
        UserAchievement userAchievement = UserAchievement.builder()
                .userId(userId)
                .achievement(achievement)
                .isDisplayed(false)
                .build();

        userAchievementRepository.save(userAchievement);
        return true;
    }
}