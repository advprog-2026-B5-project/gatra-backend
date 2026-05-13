package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.model.StudentMilestoneProgress;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.achievement.service.UserAchievementService;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.achievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.StudentMilestoneProgressRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.achievement.service.MilestoneServiceImpl;
import id.ac.ui.cs.advprog.gatra.auth.service.UserService;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
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
class MilestoneServiceImplTest {

    @Mock private StudentMilestoneProgressRepository milestoneProgressRepository;
    @Mock private AchievementRepository achievementRepository;
    @Mock private UserAchievementRepository userAchievementRepository;
    @Mock private UserService userService;
    @Mock private UserAchievementService userAchievementService;
    @Mock private AchievementMapper achievementMapper;

    @InjectMocks
    private MilestoneServiceImpl milestoneService;

    private UUID userId;
    private User user;
    private Achievement achievement5;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .username("student1")
                .build();

        achievement5 = Achievement.builder()
                .id(UUID.randomUUID())
                .name("Pembaca Pemula")
                .category(ActionType.READ_ARTICLE)
                .milestoneThreshold(5)
                .description("Baca 5 artikel")
                .build();
    }

    @Test
    void recordAction_shouldIncrementProgressAndReturnResponse() {
        StudentMilestoneProgress existingProgress = StudentMilestoneProgress.builder()
                .id(UUID.randomUUID())
                .user(user)
                .actionType(ActionType.READ_ARTICLE)
                .totalCount(2)
                .build();

        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(milestoneProgressRepository.findByUserIdAndActionType(userId, ActionType.READ_ARTICLE))
                .thenReturn(Optional.of(existingProgress));
        when(milestoneProgressRepository.save(any())).thenReturn(existingProgress);
        when(achievementRepository.findByCategoryAndMilestoneThresholdLessThanEqual(
                ActionType.READ_ARTICLE, 3)).thenReturn(List.of());

        MilestoneResponse result = milestoneService.recordAction(userId, ActionType.READ_ARTICLE);

        assertEquals("READ_ARTICLE", result.getActionType());
        assertEquals(3, result.getNewTotalCount());
        assertTrue(result.getNewlyUnlockedAchievements().isEmpty());
        verify(milestoneProgressRepository).save(existingProgress);
    }

    @Test
    void recordAction_whenNewUser_shouldCreateProgress() {
        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(milestoneProgressRepository.findByUserIdAndActionType(userId, ActionType.READ_ARTICLE))
                .thenReturn(Optional.empty());
        when(milestoneProgressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(achievementRepository.findByCategoryAndMilestoneThresholdLessThanEqual(
                ActionType.READ_ARTICLE, 1)).thenReturn(List.of());

        MilestoneResponse result = milestoneService.recordAction(userId, ActionType.READ_ARTICLE);

        assertEquals(1, result.getNewTotalCount());
        verify(milestoneProgressRepository).save(any(StudentMilestoneProgress.class));
    }

    @Test
    void recordAction_shouldUnlockAchievementWhenThresholdReached() {
        StudentMilestoneProgress existingProgress = StudentMilestoneProgress.builder()
                .id(UUID.randomUUID())
                .user(user)
                .actionType(ActionType.READ_ARTICLE)
                .totalCount(4)
                .build();

        AchievementResponse achievementResponse = AchievementResponse.builder()
                .id(achievement5.getId())
                .name("Pembaca Pemula")
                .category(ActionType.READ_ARTICLE)
                .milestoneThreshold(5)
                .build();

        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(milestoneProgressRepository.findByUserIdAndActionType(userId, ActionType.READ_ARTICLE))
                .thenReturn(Optional.of(existingProgress));
        when(milestoneProgressRepository.save(any())).thenReturn(existingProgress);
        when(achievementRepository.findByCategoryAndMilestoneThresholdLessThanEqual(
                ActionType.READ_ARTICLE, 5)).thenReturn(List.of(achievement5));

        when(userAchievementService.unlockIfNotYet(eq(userId), eq(achievement5)))
                .thenReturn(true);

        when(achievementMapper.toResponse(achievement5))
                .thenReturn(achievementResponse);

        MilestoneResponse result = milestoneService.recordAction(userId, ActionType.READ_ARTICLE);

        assertEquals(5, result.getNewTotalCount());
        assertEquals(1, result.getNewlyUnlockedAchievements().size(), "List achievement baru tidak boleh kosong");
        assertEquals("Pembaca Pemula", result.getNewlyUnlockedAchievements().get(0).getName());
    }

    @Test
    void recordAction_shouldNotUnlockAlreadyOwnedAchievement() {
        StudentMilestoneProgress existingProgress = StudentMilestoneProgress.builder()
                .id(UUID.randomUUID())
                .user(user)
                .actionType(ActionType.READ_ARTICLE)
                .totalCount(5)
                .build();

        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(milestoneProgressRepository.findByUserIdAndActionType(userId, ActionType.READ_ARTICLE))
                .thenReturn(Optional.of(existingProgress));
        when(milestoneProgressRepository.save(any())).thenReturn(existingProgress);
        when(achievementRepository.findByCategoryAndMilestoneThresholdLessThanEqual(
                ActionType.READ_ARTICLE, 6)).thenReturn(List.of(achievement5));

        MilestoneResponse result = milestoneService.recordAction(userId, ActionType.READ_ARTICLE);

        assertEquals(6, result.getNewTotalCount());
        assertTrue(result.getNewlyUnlockedAchievements().isEmpty());
        verify(userAchievementRepository, never()).save(any());
    }

    @Test
    void recordAction_whenUserNotFound_shouldThrowException() {
        when(userService.getUserEntityById(userId)).thenThrow(new ResourceNotFoundException("User", userId));

        assertThrows(ResourceNotFoundException.class,
                () -> milestoneService.recordAction(userId, ActionType.READ_ARTICLE));

        verify(milestoneProgressRepository, never()).save(any());
    }
}