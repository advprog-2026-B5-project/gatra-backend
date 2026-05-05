package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.gatra.achievement.service.MissionProgressServiceImpl;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.MissionProgressMapper;
import id.ac.ui.cs.advprog.gatra.model.*;
import id.ac.ui.cs.advprog.gatra.achievement.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserMissionProgressRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
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
class MissionProgressServiceImplTest {

    @Mock private DailyMissionRepository dailyMissionRepository;
    @Mock private UserMissionProgressRepository progressRepository;
    @Mock private UserRepository userRepository;
    @Mock private MissionProgressMapper progressMapper;

    @InjectMocks
    private MissionProgressServiceImpl missionProgressService;

    private UUID userId;
    private UUID missionId;
    private User user;
    private DailyMission mission;
    private UserMissionProgress progress;
    private MissionProgressResponse responseDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        missionId = UUID.randomUUID();

        user = User.builder().id(userId).username("student1").build();

        mission = DailyMission.builder()
                .id(missionId)
                .title("Baca Artikel")
                .description("Baca 3 artikel hari ini")
                .actionType(ActionType.READ_ARTICLE)
                .targetCount(3)
                .rewardPoints(50)
                .status(MissionStatus.ACTIVE)
                .build();

        progress = UserMissionProgress.builder()
                .id(UUID.randomUUID())
                .user(user)
                .mission(mission)
                .currentCount(1)
                .isClaimed(false)
                .build();

        responseDto = MissionProgressResponse.builder()
                .missionId(missionId)
                .title("Baca Artikel")
                .description("Baca 3 artikel hari ini")
                .actionType("READ_ARTICLE")
                .targetCount(3)
                .rewardPoints(50)
                .currentCount(1)
                .isClaimed(false)
                .isCompleted(false)
                .build();
    }

    @Test
    void getActiveMissionsWithProgress_shouldReturnMissionsWithProgress() {
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId))
                .thenReturn(Optional.of(progress));
        when(progressMapper.toResponse(mission, progress)).thenReturn(responseDto);

        List<MissionProgressResponse> result = missionProgressService.getActiveMissionsWithProgress(userId);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCurrentCount());
        assertEquals(3, result.get(0).getTargetCount());
        verify(dailyMissionRepository).findByStatus(MissionStatus.ACTIVE);
    }

    @Test
    void getActiveMissionsWithProgress_whenNoProgress_shouldReturnZeroCount() {
        MissionProgressResponse zeroResponse = MissionProgressResponse.builder()
                .missionId(missionId).currentCount(0).targetCount(3).isCompleted(false).build();

        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId))
                .thenReturn(Optional.empty());
        when(progressMapper.toResponse(mission, null)).thenReturn(zeroResponse);

        List<MissionProgressResponse> result = missionProgressService.getActiveMissionsWithProgress(userId);

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getCurrentCount());
    }

    @Test
    void getActiveMissionsWithProgress_whenNoActiveMissions_shouldReturnEmpty() {
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of());

        List<MissionProgressResponse> result = missionProgressService.getActiveMissionsWithProgress(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    void incrementProgress_shouldIncrementCount() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId))
                .thenReturn(Optional.of(progress));

        missionProgressService.incrementProgress(userId, "READ_ARTICLE");

        assertEquals(2, progress.getCurrentCount());
        verify(progressRepository).save(progress);
    }

    @Test
    void incrementProgress_whenNoProgressExists_shouldCreateNew() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId))
                .thenReturn(Optional.empty());

        missionProgressService.incrementProgress(userId, "READ_ARTICLE");

        verify(progressRepository).save(any(UserMissionProgress.class));
    }

    @Test
    void incrementProgress_whenAlreadyComplete_shouldNotIncrement() {
        progress.setCurrentCount(3);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId))
                .thenReturn(Optional.of(progress));

        missionProgressService.incrementProgress(userId, "READ_ARTICLE");

        assertEquals(3, progress.getCurrentCount());
        verify(progressRepository, never()).save(any());
    }

    @Test
    void incrementProgress_whenDifferentActionType_shouldNotIncrement() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));

        missionProgressService.incrementProgress(userId, "FINISH_QUIZ");

        verify(progressRepository, never()).save(any());
    }

    @Test
    void incrementProgress_whenUserNotFound_shouldThrow() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> missionProgressService.incrementProgress(userId, "READ_ARTICLE"));
    }

    @Test
    void claimReward_whenCompleted_shouldSetClaimed() {
        progress.setCurrentCount(3);
        MissionProgressResponse claimedResponse = MissionProgressResponse.builder()
                .missionId(missionId).currentCount(3).isClaimed(true).isCompleted(true).build();

        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId))
                .thenReturn(Optional.of(progress));
        when(progressMapper.toResponse(mission, progress)).thenReturn(claimedResponse);

        MissionProgressResponse result = missionProgressService.claimReward(userId, missionId);

        assertTrue(result.getIsClaimed());
        verify(progressRepository).save(progress);
    }

    @Test
    void claimReward_whenNotCompleted_shouldThrow() {
        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId))
                .thenReturn(Optional.of(progress));

        assertThrows(IllegalArgumentException.class,
                () -> missionProgressService.claimReward(userId, missionId));
    }

    @Test
    void claimReward_whenAlreadyClaimed_shouldThrow() {
        progress.setCurrentCount(3);
        progress.setIsClaimed(true);

        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId))
                .thenReturn(Optional.of(progress));

        assertThrows(IllegalArgumentException.class,
                () -> missionProgressService.claimReward(userId, missionId));
    }

    @Test
    void claimReward_whenMissionNotFound_shouldThrow() {
        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> missionProgressService.claimReward(userId, missionId));
    }

    @Test
    void claimReward_whenNoProgress_shouldThrow() {
        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> missionProgressService.claimReward(userId, missionId));
    }
}