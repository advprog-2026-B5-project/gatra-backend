package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.model.Achievement;
import id.ac.ui.cs.advprog.gatra.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementMapper achievementMapper;

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
        if (achievementRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Achievement dengan nama '" + request.getName() + "' sudah ada");
        }

        Achievement achievement = Achievement.builder()
                .name(request.getName())
                .category(request.getCategory())
                .milestoneThreshold(request.getMilestoneThreshold())
                .description(request.getDescription())
                .badgeUrl(request.getBadgeUrl())
                .build();

        return achievementMapper.toResponse(achievementRepository.save(achievement));
    }

    @Override
    @Transactional
    public AchievementResponse updateAchievement(UUID id, AchievementRequest request) {
        Achievement achievement = findAchievementOrThrow(id);

        if (!achievement.getName().equals(request.getName())
                && achievementRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Achievement dengan nama '" + request.getName() + "' sudah ada");
        }

        achievement.setName(request.getName());
        achievement.setCategory(request.getCategory());
        achievement.setMilestoneThreshold(request.getMilestoneThreshold());
        achievement.setDescription(request.getDescription());
        achievement.setBadgeUrl(request.getBadgeUrl());

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
        List<UserAchievement> relations = userAchievementRepository.findByUserUsername(username);

        return relations.stream()
                .map(relation -> {
                    AchievementResponse response = achievementMapper.toResponse(relation.getAchievement());
                    response.setUnlockedAt(relation.getUnlockedAt().toString());
                    response.setDisplayed(relation.isDisplayed());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AchievementResponse> getDisplayedAchievements(String username) {
        return userAchievementRepository.findByUserUsernameAndIsDisplayedTrue(username)
                .stream()
                .map(rel -> {
                    AchievementResponse res = achievementMapper.toResponse(rel.getAchievement());
                    res.setDisplayed(true);
                    return res;
                })
                // Batasi maksimal 3 agar dropdown tidak kepanjangan
                .limit(3)
                .collect(Collectors.toList());
    }
}