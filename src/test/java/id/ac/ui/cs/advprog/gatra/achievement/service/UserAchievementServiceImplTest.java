package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.strategy.HideAchievementStrategy;
import id.ac.ui.cs.advprog.gatra.achievement.strategy.ShowAchievementStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private AchievementMapper achievementMapper;
    @Mock
    private ShowAchievementStrategy showStrategy;
    @Mock
    private HideAchievementStrategy hideStrategy;

    @InjectMocks
    private UserAchievementServiceImpl userAchievementService;

    private final String USERNAME = "user123";
    private UUID achievementId;
    private UserAchievement userAchievement;

    @BeforeEach
    void setUp() {
        achievementId = UUID.randomUUID();
        userAchievement = new UserAchievement();
    }

    @Test
    void getMyAchievements_ShouldReturnListOfResponse() {
        String username = "testUser";
        Achievement ach = Achievement.builder().name("Master Kuis").build();

        UserAchievement relation = UserAchievement.builder()
                .achievement(ach)
                .unlockedAt(java.time.LocalDateTime.now())
                .isDisplayed(true)
                .build();

        AchievementResponse response = AchievementResponse.builder().name("Master Kuis").build();

        when(userAchievementRepository.findByUserUsername(username)).thenReturn(List.of(relation));
        when(achievementMapper.toResponseFromUserAchievement(relation)).thenReturn(response);

        List<AchievementResponse> result = userAchievementService.getMyAchievements(username);

        assertFalse(result.isEmpty());
        assertEquals("Master Kuis", result.get(0).getName());
        verify(userAchievementRepository).findByUserUsername(username);
    }

    @Test
    void getDisplayedAchievements_shouldReturnLimitedList() {
        String username = "rehema";
        Achievement ach = Achievement.builder().name("Test").build();
        UserAchievement rel1 = UserAchievement.builder().achievement(ach).build();
        UserAchievement rel2 = UserAchievement.builder().achievement(ach).build();
        UserAchievement rel3 = UserAchievement.builder().achievement(ach).build();
        UserAchievement rel4 = UserAchievement.builder().achievement(ach).build();

        when(userAchievementRepository.findByUserUsernameAndIsDisplayedTrue(username))
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
        verify(userAchievementRepository, times(1)).findByUserUsernameAndIsDisplayedTrue(username);
    }

    @Test
    void toggleDisplay_whenDisplayedTrue_shouldUseShowStrategy() {
        when(userAchievementRepository.findByUserUsernameAndAchievementId(USERNAME, achievementId))
                .thenReturn(Optional.of(userAchievement));

        userAchievementService.toggleDisplayAchievement(USERNAME, achievementId, true);

        verify(showStrategy, times(1)).execute(userAchievement, userAchievementRepository);
        verify(hideStrategy, never()).execute(any(), any());
    }

    @Test
    void toggleDisplay_whenDisplayedFalse_shouldUseHideStrategy() {
        when(userAchievementRepository.findByUserUsernameAndAchievementId(USERNAME, achievementId))
                .thenReturn(Optional.of(userAchievement));

        userAchievementService.toggleDisplayAchievement(USERNAME, achievementId, false);

        verify(hideStrategy, times(1)).execute(userAchievement, userAchievementRepository);
        verify(showStrategy, never()).execute(any(), any());
    }

    @Test
    void toggleDisplay_whenNotFound_shouldThrowException() {
        when(userAchievementRepository.findByUserUsernameAndAchievementId(USERNAME, achievementId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            userAchievementService.toggleDisplayAchievement(USERNAME, achievementId, true)
        );
    }
}