package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.service.UserService;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.strategy.DisplayAchievementStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAchievementServiceImplTest {

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private UserService userService;

    @Mock
    private AchievementMapper achievementMapper;

    @Mock
    private DisplayAchievementStrategy showStrategy;
    @Mock
    private DisplayAchievementStrategy hideStrategy;

    private UserAchievementServiceImpl userAchievementService;

    private final String USERNAME = "user123";
    private UUID achievementId;
    private UUID userId;
    private User mockUser;
    private UserAchievement userAchievement;

    @BeforeEach
    void setUp() {
        achievementId = UUID.randomUUID();
        userId = UUID.randomUUID();

        mockUser = User.builder().id(userId).username(USERNAME).build();

        userAchievement = new UserAchievement();
        userAchievement.setUserId(userId);

        userAchievementService = new UserAchievementServiceImpl(
                userAchievementRepository,
                userService,
                achievementMapper,
                showStrategy,
                hideStrategy
        );
    }

    @Test
    void getMyAchievements_ShouldReturnListOfResponse() {
        String username = "testUser";
        UUID currentUserId = UUID.randomUUID();
        User currentUser = User.builder().id(currentUserId).username(username).build();

        Achievement ach = Achievement.builder().name("Master Kuis").build();

        UserAchievement relation = UserAchievement.builder()
                .userId(currentUserId)
                .achievement(ach)
                .unlockedAt(java.time.LocalDateTime.now())
                .isDisplayed(true)
                .build();

        AchievementResponse response = AchievementResponse.builder().name("Master Kuis").build();

        when(userService.getUserEntityByUsername(username)).thenReturn(currentUser);
        when(userAchievementRepository.findByUserId(currentUserId)).thenReturn(List.of(relation));
        when(achievementMapper.toResponseFromUserAchievement(relation)).thenReturn(response);

        List<AchievementResponse> result = userAchievementService.getMyAchievements(username);

        assertFalse(result.isEmpty());
        assertEquals("Master Kuis", result.get(0).getName());

        verify(userService).getUserEntityByUsername(username);
        verify(userAchievementRepository).findByUserId(currentUserId);
    }

    @Test
    void getDisplayedAchievements_shouldReturnLimitedList() {
        String username = "rehema";
        UUID currentUserId = UUID.randomUUID();
        User currentUser = User.builder().id(currentUserId).username(username).build();

        Achievement ach = Achievement.builder().name("Test").build();
        UserAchievement rel1 = UserAchievement.builder().achievement(ach).build();
        UserAchievement rel2 = UserAchievement.builder().achievement(ach).build();
        UserAchievement rel3 = UserAchievement.builder().achievement(ach).build();
        UserAchievement rel4 = UserAchievement.builder().achievement(ach).build();

        when(userService.getUserEntityByUsername(username)).thenReturn(currentUser);
        when(userAchievementRepository.findByUserIdAndIsDisplayedTrue(currentUserId))
                .thenReturn(List.of(rel1, rel2, rel3, rel4));

        AchievementResponse displayedResponse = AchievementResponse.builder()
                .name("Test")
                .isDisplayed(true)
                .build();

        when(achievementMapper.toResponseFromUserAchievement(any()))
                .thenReturn(displayedResponse);

        List<AchievementResponse> result = userAchievementService.getDisplayedAchievements(username);

        assertEquals(3, result.size());
        assertTrue(result.get(0).isDisplayed());

        verify(userService).getUserEntityByUsername(username);
        verify(userAchievementRepository, times(1)).findByUserIdAndIsDisplayedTrue(currentUserId);
    }

    @Test
    void toggleDisplay_whenDisplayedTrue_shouldUseShowStrategy() {
        when(userService.getUserEntityByUsername(USERNAME)).thenReturn(mockUser);
        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId))
                .thenReturn(Optional.of(userAchievement));

        userAchievementService.toggleDisplayAchievement(USERNAME, achievementId, true);

        verify(userService).getUserEntityByUsername(USERNAME);
        verify(showStrategy, times(1)).execute(userAchievement);
        verify(hideStrategy, never()).execute(any());
    }

    @Test
    void toggleDisplay_whenDisplayedFalse_shouldUseHideStrategy() {
        when(userService.getUserEntityByUsername(USERNAME)).thenReturn(mockUser);
        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId))
                .thenReturn(Optional.of(userAchievement));

        userAchievementService.toggleDisplayAchievement(USERNAME, achievementId, false);

        verify(userService).getUserEntityByUsername(USERNAME);
        verify(hideStrategy, times(1)).execute(userAchievement);
        verify(showStrategy, never()).execute(any());
    }

    @Test
    void toggleDisplay_whenNotFound_shouldThrowException() {
        when(userService.getUserEntityByUsername(USERNAME)).thenReturn(mockUser);
        when(userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userAchievementService.toggleDisplayAchievement(USERNAME, achievementId, true)
        );

        verify(userService).getUserEntityByUsername(USERNAME);
    }

    @Test
    void unlockIfNotYet_ShouldReturnFalse_WhenAlreadyUnlocked() {
        Achievement ach = Achievement.builder().id(achievementId).build();

        when(userAchievementRepository.existsByUserIdAndAchievementId(userId, achievementId))
                .thenReturn(true);

        boolean result = userAchievementService.unlockIfNotYet(userId, ach);

        assertFalse(result);
        verify(userAchievementRepository, never()).save(any());
    }

    @Test
    void unlockIfNotYet_ShouldReturnTrue_WhenNotUnlocked() {
        Achievement ach = Achievement.builder().id(achievementId).build();

        when(userAchievementRepository.existsByUserIdAndAchievementId(userId, achievementId))
                .thenReturn(false);

        boolean result = userAchievementService.unlockIfNotYet(userId, ach);

        assertTrue(result);
        verify(userAchievementRepository, times(1)).save(any(UserAchievement.class));
    }
}