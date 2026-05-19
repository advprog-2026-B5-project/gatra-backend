package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.MissionProgressMapper;
import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.gatra.achievement.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserMissionProgressRepository;
import id.ac.ui.cs.advprog.gatra.auth.service.UserService;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.scoring.model.PointActivityType;
import id.ac.ui.cs.advprog.gatra.scoring.service.PointRecordingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionProgressScoringIntegrationTest {

    @Mock private DailyMissionRepository dailyMissionRepository;
    @Mock private UserMissionProgressRepository progressRepository;
    @Mock private UserService userService;
    @Mock private MissionProgressMapper progressMapper;
    @Mock private ClanMembershipRepository clanMembershipRepository;
    @Mock private PointRecordingService pointRecordingService;

    @InjectMocks
    private MissionProgressServiceImpl missionProgressService;

    private UUID userId;
    private String username;
    private UUID missionId;
    private User mockUser;
    private DailyMission mockMission;
    private UserMissionProgress mockProgress;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        username = "user";
        missionId = UUID.randomUUID();

        mockUser = User.builder()
                .id(userId)
                .username(username)
                .build();

        mockMission = new DailyMission();
        mockMission.setId(missionId);
        mockMission.setTargetCount(5);
        mockMission.setRewardPoints(50);

        mockProgress = new UserMissionProgress();
        mockProgress.setUser(mockUser);
        mockProgress.setMission(mockMission);
        mockProgress.setCurrentCount(5);
        mockProgress.setIsClaimed(false);

        when(userService.getUserEntityByUsername(username)).thenReturn(mockUser);
        when(dailyMissionRepository.findById(missionId)).thenReturn(Optional.of(mockMission));
        when(progressRepository.findByUserIdAndMissionId(userId, missionId)).thenReturn(Optional.of(mockProgress));
        when(progressMapper.toResponse(any(), any())).thenReturn(new MissionProgressResponse());
    }

    @Test
    void claimReward_UserInClan_RecordsMissionPoints() {
        // Arrange
        Clan mockClan = new Clan();
        mockClan.setId("clan-999");

        ClanMembership mockMembership = new ClanMembership();
        mockMembership.setClan(mockClan);

        when(clanMembershipRepository.findFirstByUserIdAndStatus(userId.toString(), MembershipStatus.APPROVED))
                .thenReturn(Optional.of(mockMembership));

        // Act
        missionProgressService.claimReward(username, missionId);

        // Assert
        verify(pointRecordingService, times(1)).recordPoints(
                userId.toString(),
                "clan-999",
                50.0,
                PointActivityType.DAILY_MISSION_COMPLETED,
                missionId.toString()
        );

        verify(progressRepository).save(argThat(progress -> progress.getIsClaimed()));
    }

    @Test
    void claimReward_UserNotInClan_DoesNotRecordPoints() {
        // Arrange
        when(clanMembershipRepository.findFirstByUserIdAndStatus(userId.toString(), MembershipStatus.APPROVED))
                .thenReturn(Optional.empty());

        // Act
        missionProgressService.claimReward(username, missionId);

        // Assert
        verify(pointRecordingService, never()).recordPoints(anyString(), anyString(), anyDouble(), any(), anyString());
        verify(progressRepository).save(argThat(progress -> progress.getIsClaimed()));
    }
}