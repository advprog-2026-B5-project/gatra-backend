package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.model.Achievement;
import id.ac.ui.cs.advprog.gatra.repository.AchievementRepository;
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
}