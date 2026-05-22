package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.service.UserService;
import id.ac.ui.cs.advprog.gatra.achievement.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.achievement.event.MissionRewardClaimedEvent;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.MissionProgressMapper;
import id.ac.ui.cs.advprog.gatra.achievement.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserMissionProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
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
    @Mock private UserService userService;
    @Mock private MissionProgressMapper progressMapper;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MissionProgressServiceImpl missionProgressService;

    private UUID userId;
    private UUID missionId;
    private User user;
    private DailyMission mission;
    private UserMissionProgress progress;
    private MissionProgressResponse responseDto;
    private String username;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        missionId = UUID.randomUUID();
        username = "student1";

        user = User.builder().id(userId).username(username).build();

        mission = DailyMission.builder()
                .id(missionId)
                .title("Baca Artikel")
                .actionType(ActionType.READ_ARTICLE)
                .targetCount(3)
                .rewardPoints(100)
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
                .currentCount(1)
                .targetCount(3)
                .isClaimed(false)
                .build();
    }
    @Test
    void getActiveMissionsWithProgress_shouldReturnMissionsWithProgress() {
        when(userService.getUserEntityByUsername(username)).thenReturn(user);
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(progress));
        when(progressMapper.toResponse(mission, progress)).thenReturn(responseDto);

        List<MissionProgressResponse> result = missionProgressService.getActiveMissionsWithProgress(username);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCurrentCount());
        verify(userService).getUserEntityByUsername(username);
        verify(dailyMissionRepository).findByStatus(MissionStatus.ACTIVE);
        verify(progressRepository).findByUserIdAndMissionId(userId, missionId);
    }

    @Test
    void getActiveMissionsWithProgress_whenNoProgress_shouldReturnMissionsWithNullProgress() {
        when(userService.getUserEntityByUsername(username)).thenReturn(user);
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.empty());
        when(progressMapper.toResponse(mission, null)).thenReturn(responseDto);

        List<MissionProgressResponse> result = missionProgressService.getActiveMissionsWithProgress(username);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(progressMapper).toResponse(mission, null);
    }

    @Test
    void getActiveMissionsWithProgress_whenNoActiveMissions_shouldReturnEmptyList() {
        when(userService.getUserEntityByUsername(username)).thenReturn(user);
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(Collections.emptyList());

        List<MissionProgressResponse> result = missionProgressService.getActiveMissionsWithProgress(username);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void incrementProgress_shouldIncrementCount() {
        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(progress));

        missionProgressService.incrementProgress(userId, "READ_ARTICLE");

        assertEquals(2, progress.getCurrentCount());
        verify(progressRepository).save(progress);
    }

    @Test
    void incrementProgress_whenNoProgressExists_shouldCreateNew() {
        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.empty());

        missionProgressService.incrementProgress(userId, "READ_ARTICLE");

        verify(progressRepository).save(any(UserMissionProgress.class));
    }

    @Test
    void incrementProgress_whenUserNotFound_shouldThrow() {
        when(userService.getUserEntityById(userId)).thenThrow(new ResourceNotFoundException("User", userId));

        assertThrows(ResourceNotFoundException.class,
            () -> missionProgressService.incrementProgress(userId, "READ_ARTICLE"));
    }

    @Test
    void incrementProgress_shouldAddToList_whenMissionJustCompleted() {
        mission.setTargetCount(3);
        progress.setCurrentCount(2);

        MissionProgressResponse completedResponse = MissionProgressResponse.builder()
                .missionId(missionId)
                .title("Baca Artikel")
                .currentCount(3)
                .targetCount(3)
                .isCompleted(true)
                .build();
        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId))
                .thenReturn(Optional.of(progress));
        when(progressMapper.toResponse(mission, progress)).thenReturn(completedResponse);

        List<MissionProgressResponse> result = missionProgressService.incrementProgress(userId, "READ_ARTICLE");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getCurrentCount());
        assertTrue(result.get(0).getIsCompleted());
        verify(progressRepository).save(progress);
        verify(progressMapper).toResponse(mission, progress);
    }

    @Test
    void incrementProgress_whenProgressAlreadyCompleted_shouldNotIncrement() {
        progress.setCurrentCount(3);
        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(progress));

        List<MissionProgressResponse> result = missionProgressService.incrementProgress(userId, "READ_ARTICLE");

        assertTrue(result.isEmpty());
        assertEquals(3, progress.getCurrentCount());
        verify(progressRepository, never()).save(any());
    }

    @Test
    void incrementProgress_whenNoMatchingMissions_shouldReturnEmpty() {
        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));

        List<MissionProgressResponse> result = missionProgressService.incrementProgress(userId, "FINISH_QUIZ");

        assertTrue(result.isEmpty());
        verify(progressRepository, never()).save(any());
    }

    @Test
    void incrementProgress_incrementsButNotYetCompleted_shouldNotAddToList() {
        progress.setCurrentCount(0);
        mission.setTargetCount(3);

        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(progress));

        List<MissionProgressResponse> result = missionProgressService.incrementProgress(userId, "READ_ARTICLE");

        assertTrue(result.isEmpty());
        assertEquals(1, progress.getCurrentCount());
        verify(progressRepository).save(progress);
        verify(progressMapper, never()).toResponse(any(), any());
    }

    @Test
    void claimReward_whenCompleted_shouldSetClaimedAndPublishEvent() {
        progress.setCurrentCount(3);
        MissionProgressResponse claimedResponse = MissionProgressResponse.builder()
                .missionId(missionId).isClaimed(true).build();

        when(userService.getUserEntityByUsername(username)).thenReturn(user);
        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(progress));
        when(progressMapper.toResponse(mission, progress)).thenReturn(claimedResponse);

        MissionProgressResponse result = missionProgressService.claimReward(username, missionId);

        assertTrue(result.getIsClaimed());
        assertTrue(progress.getIsClaimed());
        verify(progressRepository).save(progress);

        ArgumentCaptor<MissionRewardClaimedEvent> eventCaptor = ArgumentCaptor.forClass(MissionRewardClaimedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        MissionRewardClaimedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(userId, publishedEvent.userId());
        assertEquals(missionId, publishedEvent.missionId());
        assertEquals(100, publishedEvent.rewardPoints());
    }

    @Test
    void claimReward_whenRewardPointsNull_shouldPublishEventWithZero() {
        progress.setCurrentCount(3);
        mission.setRewardPoints(null);

        MissionProgressResponse claimedResponse = MissionProgressResponse.builder()
                .missionId(missionId).isClaimed(true).build();

        when(userService.getUserEntityByUsername(username)).thenReturn(user);
        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(progress));
        when(progressMapper.toResponse(mission, progress)).thenReturn(claimedResponse);

        missionProgressService.claimReward(username, missionId);

        ArgumentCaptor<MissionRewardClaimedEvent> eventCaptor = ArgumentCaptor.forClass(MissionRewardClaimedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(0, eventCaptor.getValue().rewardPoints());
    }

    @Test
    void claimReward_whenNotCompleted_shouldThrow() {
        when(userService.getUserEntityByUsername(username)).thenReturn(user);
        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(progress));

        assertThrows(IllegalArgumentException.class,
            () -> missionProgressService.claimReward(username, missionId));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void claimReward_whenAlreadyClaimed_shouldThrow() {
        progress.setCurrentCount(3);
        progress.setIsClaimed(true);

        when(userService.getUserEntityByUsername(username)).thenReturn(user);
        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(progress));

        assertThrows(IllegalArgumentException.class,
            () -> missionProgressService.claimReward(username, missionId));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void claimReward_whenNoProgress_shouldThrow() {
        when(userService.getUserEntityByUsername(username)).thenReturn(user);
        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> missionProgressService.claimReward(username, missionId));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void claimReward_whenMissionNotFound_shouldThrow() {
        when(userService.getUserEntityByUsername(username)).thenReturn(user);
        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> missionProgressService.claimReward(username, missionId));
        verify(eventPublisher, never()).publishEvent(any());
    }
}