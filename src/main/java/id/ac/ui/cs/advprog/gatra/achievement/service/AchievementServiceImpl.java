package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.AchievementRepository;
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