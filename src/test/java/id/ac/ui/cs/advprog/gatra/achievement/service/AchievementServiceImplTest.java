package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

    private static final String ACHIEVEMENT_NAME = "tes";
    private static final String UPDATED_NAME = "Baca Buku";
    private static final ActionType CATEGORY = ActionType.READ_ARTICLE;
    private static final Integer MILESTONE = 10;
    private static final Integer UPDATED_MILESTONE = 50;
    private static final String DESCRIPTION = "Baca 10 artikel";
    private static final String BADGE_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQm9d2Z1IVPte9tDEJnCkPChtLHbTo8NdZZBg&s";

    @Mock private AchievementRepository achievementRepository;
    @Mock private AchievementMapper achievementMapper;
    @Mock private UserAchievementRepository userAchievementRepository;

    @InjectMocks
    private AchievementServiceImpl achievementService;

    private UUID achievementId;
    private Achievement achievement;
    private AchievementRequest request;
    private AchievementResponse response;

    @BeforeEach
    void setUp() {
        achievementId = UUID.randomUUID();

        achievement = Achievement.builder()
                .id(achievementId)
                .name(ACHIEVEMENT_NAME)
                .category(CATEGORY)
                .milestoneThreshold(MILESTONE)
                .description(DESCRIPTION)
                .badgeUrl(BADGE_URL)
                .build();

        request = new AchievementRequest();
        request.setName(ACHIEVEMENT_NAME);
        request.setCategory(CATEGORY);
        request.setMilestoneThreshold(MILESTONE);
        request.setDescription(DESCRIPTION);
        request.setBadgeUrl(BADGE_URL);

        response = AchievementResponse.builder()
                .id(achievementId)
                .name(ACHIEVEMENT_NAME)
                .category(CATEGORY)
                .milestoneThreshold(MILESTONE)
                .description(DESCRIPTION)
                .badgeUrl(BADGE_URL)
                .build();
    }

    @Test
    void getAllAchievements_shouldReturnListOfAchievements() {
        when(achievementRepository.findAll()).thenReturn(List.of(achievement));
        when(achievementMapper.toResponse(achievement)).thenReturn(response);

        List<AchievementResponse> result = achievementService.getAllAchievements();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
        verify(achievementRepository, times(1)).findAll();
    }

    @Test
    void getAllAchievements_whenEmpty_shouldReturnEmptyList() {
        when(achievementRepository.findAll()).thenReturn(List.of());

        List<AchievementResponse> result = achievementService.getAllAchievements();

        assertTrue(result.isEmpty());
        verify(achievementMapper, never()).toResponse(any());
    }

    @Test
    void getAchievementById_whenFound_shouldReturnAchievement() {
        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(achievement));
        when(achievementMapper.toResponse(achievement)).thenReturn(response);

        AchievementResponse result = achievementService.getAchievementById(achievementId);

        assertEquals(response, result);
        verify(achievementRepository, times(1)).findById(achievementId);
    }

    @Test
    void getAchievementById_whenNotFound_shouldThrowException() {
        when(achievementRepository.findById(achievementId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> achievementService.getAchievementById(achievementId));

        verify(achievementMapper, never()).toResponse(any());
    }

    @Test
    void createAchievement_whenValid_shouldReturnCreatedAchievement() {
        when(achievementRepository.existsByName(ACHIEVEMENT_NAME)).thenReturn(false);
        when(achievementMapper.toEntity(request)).thenReturn(achievement);
        when(achievementRepository.save(any(Achievement.class))).thenReturn(achievement);
        when(achievementMapper.toResponse(achievement)).thenReturn(response);

        AchievementResponse result = achievementService.createAchievement(request);

        assertEquals(response, result);
        verify(achievementRepository, times(1)).save(any(Achievement.class));
    }

    @Test
    void createAchievement_whenDuplicateName_shouldThrowException() {
        when(achievementRepository.existsByName(ACHIEVEMENT_NAME)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> achievementService.createAchievement(request));

        assertTrue(exception.getMessage().contains(ACHIEVEMENT_NAME));
        verify(achievementRepository, never()).save(any());
    }

    @Test
    void updateAchievement_whenFound_shouldReturnUpdatedAchievement() {
        AchievementRequest updateRequest = new AchievementRequest();
        updateRequest.setName(UPDATED_NAME);
        updateRequest.setCategory(CATEGORY);
        updateRequest.setMilestoneThreshold(UPDATED_MILESTONE);
        updateRequest.setDescription("Baca 50 artikel");
        updateRequest.setBadgeUrl(BADGE_URL);

        AchievementResponse updatedResponse = AchievementResponse.builder()
                .id(achievementId)
                .name(UPDATED_NAME)
                .category(CATEGORY)
                .milestoneThreshold(UPDATED_MILESTONE)
                .description("Baca 50 artikel")
                .badgeUrl(BADGE_URL)
                .build();

        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(achievement));
        when(achievementRepository.existsByName(UPDATED_NAME)).thenReturn(false);
        doNothing().when(achievementMapper).updateEntity(any(),any());
        when(achievementRepository.save(any(Achievement.class))).thenReturn(achievement);
        when(achievementMapper.toResponse(achievement)).thenReturn(updatedResponse);

        AchievementResponse result = achievementService.updateAchievement(achievementId, updateRequest);

        assertEquals(UPDATED_NAME, result.getName());
        assertEquals(UPDATED_MILESTONE, result.getMilestoneThreshold());
        verify(achievementRepository, times(1)).save(any(Achievement.class));
    }

    @Test
    void updateAchievement_whenSameName_shouldNotCheckUniqueness() {
        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(achievement));
        doNothing().when(achievementMapper).updateEntity(any(), any());
        when(achievementRepository.save(any(Achievement.class))).thenReturn(achievement);
        when(achievementMapper.toResponse(achievement)).thenReturn(response);

        AchievementResponse result = achievementService.updateAchievement(achievementId, request);

        assertEquals(response, result);
        verify(achievementRepository, never()).existsByName(any());
    }

    @Test
    void updateAchievement_whenDuplicateName_shouldThrowException() {
        AchievementRequest updateRequest = new AchievementRequest();
        updateRequest.setName(UPDATED_NAME);
        updateRequest.setCategory(CATEGORY);
        updateRequest.setMilestoneThreshold(MILESTONE);

        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(achievement));
        when(achievementRepository.existsByName(UPDATED_NAME)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> achievementService.updateAchievement(achievementId, updateRequest));

        verify(achievementRepository, never()).save(any());
    }

    @Test
    void updateAchievement_whenNotFound_shouldThrowException() {
        when(achievementRepository.findById(achievementId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> achievementService.updateAchievement(achievementId, request));

        verify(achievementRepository, never()).save(any());
    }

    @Test
    void deleteAchievement_whenFound_shouldDelete() {
        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(achievement));

        assertDoesNotThrow(() -> achievementService.deleteAchievement(achievementId));

        verify(achievementRepository, times(1)).deleteById(achievementId);
    }

    @Test
    void deleteAchievement_whenNotFound_shouldThrowException() {
        when(achievementRepository.findById(achievementId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> achievementService.deleteAchievement(achievementId));

        verify(achievementRepository, never()).deleteById(any());
    }
}