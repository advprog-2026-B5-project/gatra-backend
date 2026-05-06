package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.service.strategy.DisplayAchievementStrategy;
import id.ac.ui.cs.advprog.gatra.service.strategy.HideAchievementStrategy;
import id.ac.ui.cs.advprog.gatra.service.strategy.ShowAchievementStrategy;
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
    private final AchievementMapper achievementMapper;

    private final ShowAchievementStrategy showStrategy;
    private final HideAchievementStrategy hideStrategy;

    @Override
    public List<AchievementResponse> getMyAchievements(String username) {
        return userAchievementRepository.findByUserUsername(username).stream()
                .map(achievementMapper::toResponseFromUserAchievement)
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getDisplayedAchievements(String username) {
        return userAchievementRepository.findByUserUsernameAndIsDisplayedTrue(username).stream()
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
        return userAchievementRepository
                .findByUserUsernameAndAchievementId(username, achievementId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAchievement", achievementId));
    }
}