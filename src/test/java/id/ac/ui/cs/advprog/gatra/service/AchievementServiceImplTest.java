package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.model.Achievement;
import id.ac.ui.cs.advprog.gatra.model.ActionType;
import id.ac.ui.cs.advprog.gatra.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserAchievementRepository;
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
                .category(CATEGORY.name())
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
                .category(CATEGORY.name())
                .milestoneThreshold(UPDATED_MILESTONE)
                .description("Baca 50 artikel")
                .badgeUrl(BADGE_URL)
                .build();

        when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(achievement));
        when(achievementRepository.existsByName(UPDATED_NAME)).thenReturn(false);
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

    @Test
    void getMyAchievements_ShouldReturnListOfResponse() {
        // Arrange
        String username = "testUser";
        Achievement ach = Achievement.builder().name("Master Kuis").build();

        UserAchievement relation = UserAchievement.builder()
                .achievement(ach)
                .unlockedAt(java.time.LocalDateTime.now())
                .isDisplayed(true) // Opsional: sekalian test untuk isDisplayed
                .build();

        AchievementResponse response = AchievementResponse.builder().name("Master Kuis").build();

        when(userAchievementRepository.findByUserUsername(username)).thenReturn(List.of(relation));
        when(achievementMapper.toResponse(ach)).thenReturn(response);

        // Act
        List<AchievementResponse> result = achievementService.getMyAchievements(username);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("Master Kuis", result.get(0).getName());
        verify(userAchievementRepository).findByUserUsername(username);
    }

    @Test
    void getDisplayedAchievements_shouldReturnLimitedList() {
        // Arrange: Siapkan 4 achievement yang di-pin di database
        String username = "rehema";
        Achievement ach = Achievement.builder().name("Test").build();
        UserAchievement rel1 = UserAchievement.builder().achievement(ach).build();
        UserAchievement rel2 = UserAchievement.builder().achievement(ach).build();
        UserAchievement rel3 = UserAchievement.builder().achievement(ach).build();
        UserAchievement rel4 = UserAchievement.builder().achievement(ach).build();

        when(userAchievementRepository.findByUserUsernameAndIsDisplayedTrue(username))
                .thenReturn(List.of(rel1, rel2, rel3, rel4));

        when(achievementMapper.toResponse(any())).thenReturn(response);

        // Act: Memanggil fungsi untuk Dropdown Navbar
        List<AchievementResponse> result = achievementService.getDisplayedAchievements(username);

        // Assert: Pastikan hasilnya dibatasi maksimal 3 sesuai logika `.limit(3)`
        assertEquals(3, result.size());
        assertTrue(result.get(0).isDisplayed());
        verify(userAchievementRepository, times(1)).findByUserUsernameAndIsDisplayedTrue(username);
    }
}