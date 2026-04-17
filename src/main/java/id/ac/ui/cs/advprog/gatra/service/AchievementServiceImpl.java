package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.model.Achievement;
import id.ac.ui.cs.advprog.gatra.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserAchievementRepository;
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
public class AchievementServiceImpl implements AchievementService {

    private static final int MAX_DISPLAYED_ACHIEVEMENTS = 3;

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementMapper achievementMapper;

    private final ShowAchievementStrategy showStrategy;
    private final HideAchievementStrategy hideStrategy;

    @Override
    public List<AchievementResponse> getAllAchievements() {
        return achievementRepository.findAll().stream()
                .map(achievementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AchievementResponse getAchievementById(UUID id) {
        return achievementMapper.toResponse(findAchievementOrThrow(id));
    }

    @Override
    @Transactional
    public AchievementResponse createAchievement(AchievementRequest request) {
        validateAchievementNameUnique(request.getName());
        Achievement achievement = achievementMapper.toEntity(request);
        return achievementMapper.toResponse(achievementRepository.save(achievement));
    }

    @Override
    @Transactional
    public AchievementResponse updateAchievement(UUID id, AchievementRequest request) {
        Achievement achievement = findAchievementOrThrow(id);
        validateAchievementNameUniqueForUpdate(achievement, request.getName());
        achievementMapper.updateEntity(achievement, request);
        return achievementMapper.toResponse(achievementRepository.save(achievement));
    }

    @Override
    @Transactional
    public void deleteAchievement(UUID id) {
        findAchievementOrThrow(id);
        achievementRepository.deleteById(id);
    }

    private Achievement findAchievementOrThrow(UUID id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", id));
    }

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

    private void validateAchievementNameUnique(String name) {
        if (achievementRepository.existsByName(name)) {
            throw new IllegalArgumentException("Achievement dengan nama '" + name + "' sudah ada");
        }
    }

    private void validateAchievementNameUniqueForUpdate(Achievement existing, String newName) {
        if (!existing.getName().equals(newName) && achievementRepository.existsByName(newName)) {
            throw new IllegalArgumentException("Achievement dengan nama '" + newName + "' sudah ada");
        }
    }
}