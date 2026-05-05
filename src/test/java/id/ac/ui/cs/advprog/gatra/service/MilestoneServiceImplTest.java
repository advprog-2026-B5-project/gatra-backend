package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.model.*;
import id.ac.ui.cs.advprog.gatra.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.repository.StudentMilestoneProgressRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    @Mock private UserRepository userRepository;
    @Mock private AchievementMapper achievementMapper;

    @InjectMocks
    private MilestoneServiceImpl milestoneService;

    private UUID userId;
    private User user;
    private Achievement achievement5;
    private Achievement achievement10;

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

        achievement10 = Achievement.builder()
                .id(UUID.randomUUID())
                .name("Pembaca Rajin")
                .category(ActionType.READ_ARTICLE)
                .milestoneThreshold(10)
                .description("Baca 10 artikel")
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

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
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
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
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

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(milestoneProgressRepository.findByUserIdAndActionType(userId, ActionType.READ_ARTICLE))
                .thenReturn(Optional.of(existingProgress));
        when(milestoneProgressRepository.save(any())).thenReturn(existingProgress);
        when(achievementRepository.findByCategoryAndMilestoneThresholdLessThanEqual(
                ActionType.READ_ARTICLE, 5)).thenReturn(List.of(achievement5));
        when(userAchievementRepository.existsByUserIdAndAchievementId(userId, achievement5.getId()))
                .thenReturn(false);
        when(userAchievementRepository.save(any(UserAchievement.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(achievementMapper.toResponseFromUserAchievement(any()))
                .thenReturn(achievementResponse);

        MilestoneResponse result = milestoneService.recordAction(userId, ActionType.READ_ARTICLE);

        assertEquals(5, result.getNewTotalCount());
        assertEquals(1, result.getNewlyUnlockedAchievements().size());
        assertEquals("Pembaca Pemula", result.getNewlyUnlockedAchievements().get(0).getName());
        verify(userAchievementRepository).save(any(UserAchievement.class));
    }

    @Test
    void recordAction_shouldNotUnlockAlreadyOwnedAchievement() {
        StudentMilestoneProgress existingProgress = StudentMilestoneProgress.builder()
                .id(UUID.randomUUID())
                .user(user)
                .actionType(ActionType.READ_ARTICLE)
                .totalCount(5)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(milestoneProgressRepository.findByUserIdAndActionType(userId, ActionType.READ_ARTICLE))
                .thenReturn(Optional.of(existingProgress));
        when(milestoneProgressRepository.save(any())).thenReturn(existingProgress);
        when(achievementRepository.findByCategoryAndMilestoneThresholdLessThanEqual(
                ActionType.READ_ARTICLE, 6)).thenReturn(List.of(achievement5));
        when(userAchievementRepository.existsByUserIdAndAchievementId(userId, achievement5.getId()))
                .thenReturn(true);

        MilestoneResponse result = milestoneService.recordAction(userId, ActionType.READ_ARTICLE);

        assertEquals(6, result.getNewTotalCount());
        assertTrue(result.getNewlyUnlockedAchievements().isEmpty());
        verify(userAchievementRepository, never()).save(any());
    }

    @Test
    void recordAction_shouldUnlockMultipleAchievements() {
        StudentMilestoneProgress existingProgress = StudentMilestoneProgress.builder()
                .id(UUID.randomUUID())
                .user(user)
                .actionType(ActionType.READ_ARTICLE)
                .totalCount(9)
                .build();

        AchievementResponse response5 = AchievementResponse.builder()
                .id(achievement5.getId())
                .name("Pembaca Pemula")
                .build();
        AchievementResponse response10 = AchievementResponse.builder()
                .id(achievement10.getId())
                .name("Pembaca Rajin")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(milestoneProgressRepository.findByUserIdAndActionType(userId, ActionType.READ_ARTICLE))
                .thenReturn(Optional.of(existingProgress));
        when(milestoneProgressRepository.save(any())).thenReturn(existingProgress);
        when(achievementRepository.findByCategoryAndMilestoneThresholdLessThanEqual(
                ActionType.READ_ARTICLE, 10)).thenReturn(List.of(achievement5, achievement10));
        when(userAchievementRepository.existsByUserIdAndAchievementId(userId, achievement5.getId()))
                .thenReturn(false);
        when(userAchievementRepository.existsByUserIdAndAchievementId(userId, achievement10.getId()))
                .thenReturn(false);
        when(userAchievementRepository.save(any(UserAchievement.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(achievementMapper.toResponseFromUserAchievement(any()))
                .thenReturn(response5, response10);

        MilestoneResponse result = milestoneService.recordAction(userId, ActionType.READ_ARTICLE);

        assertEquals(10, result.getNewTotalCount());
        assertEquals(2, result.getNewlyUnlockedAchievements().size());
        verify(userAchievementRepository, times(2)).save(any(UserAchievement.class));
    }

    @Test
    void recordAction_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> milestoneService.recordAction(userId, ActionType.READ_ARTICLE));

        verify(milestoneProgressRepository, never()).save(any());
    }

    @Test
    void recordAction_withFinishQuizAction_shouldWork() {
        Achievement quizAchievement = Achievement.builder()
                .id(UUID.randomUUID())
                .name("Master Kuis")
                .category(ActionType.FINISH_QUIZ)
                .milestoneThreshold(3)
                .build();

        StudentMilestoneProgress progress = StudentMilestoneProgress.builder()
                .id(UUID.randomUUID())
                .user(user)
                .actionType(ActionType.FINISH_QUIZ)
                .totalCount(2)
                .build();

        AchievementResponse quizResponse = AchievementResponse.builder()
                .id(quizAchievement.getId())
                .name("Master Kuis")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(milestoneProgressRepository.findByUserIdAndActionType(userId, ActionType.FINISH_QUIZ))
                .thenReturn(Optional.of(progress));
        when(milestoneProgressRepository.save(any())).thenReturn(progress);
        when(achievementRepository.findByCategoryAndMilestoneThresholdLessThanEqual(
                ActionType.FINISH_QUIZ, 3)).thenReturn(List.of(quizAchievement));
        when(userAchievementRepository.existsByUserIdAndAchievementId(userId, quizAchievement.getId()))
                .thenReturn(false);
        when(userAchievementRepository.save(any(UserAchievement.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(achievementMapper.toResponseFromUserAchievement(any()))
                .thenReturn(quizResponse);

        MilestoneResponse result = milestoneService.recordAction(userId, ActionType.FINISH_QUIZ);

        assertEquals("FINISH_QUIZ", result.getActionType());
        assertEquals(3, result.getNewTotalCount());
        assertEquals(1, result.getNewlyUnlockedAchievements().size());
        assertEquals("Master Kuis", result.getNewlyUnlockedAchievements().get(0).getName());
    }
}
