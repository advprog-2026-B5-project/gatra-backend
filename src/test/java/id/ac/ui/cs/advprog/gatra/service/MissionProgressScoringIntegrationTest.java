package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.mapper.MissionProgressMapper;
import id.ac.ui.cs.advprog.gatra.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.model.UserMissionProgress;
import id.ac.ui.cs.advprog.gatra.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserMissionProgressRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
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
    @Mock private UserRepository userRepository;
    @Mock private MissionProgressMapper progressMapper;

    // Core mocks for this integration test
    @Mock private ClanMembershipRepository clanMembershipRepository;
    @Mock private PointRecordingService pointRecordingService;

    @InjectMocks
    private MissionProgressServiceImpl missionProgressService;

    private UUID userId;
    private UUID missionId;
    private DailyMission mockMission;
    private UserMissionProgress mockProgress;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        missionId = UUID.randomUUID();

        // Setup a fully completed mission ready to be claimed
        mockMission = new DailyMission();
        mockMission.setId(missionId);
        mockMission.setTargetCount(5);
        mockMission.setRewardPoints(50); // Points the clan should receive

        mockProgress = new UserMissionProgress();
        mockProgress.setCurrentCount(5); // Target met
        mockProgress.setIsClaimed(false); // Not claimed yet

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

        // Mock that the user is in an active clan
        when(clanMembershipRepository.findFirstByUserIdAndStatus(userId.toString(), MembershipStatus.APPROVED))
                .thenReturn(Optional.of(mockMembership));

        // Act
        missionProgressService.claimReward(userId, missionId);

        // Assert
        verify(pointRecordingService, times(1)).recordPoints(
                userId.toString(),
                "clan-999",
                50.0, // Matches mockMission.getRewardPoints()
                PointActivityType.DAILY_MISSION_COMPLETED,
                missionId.toString()
        );

        // Also verify the progress was actually saved as claimed
        verify(progressRepository).save(argThat(progress -> progress.getIsClaimed() == true));
    }

    @Test
    void claimReward_UserNotInClan_DoesNotRecordPoints() {
        // Arrange
        // User is NOT in an active clan
        when(clanMembershipRepository.findFirstByUserIdAndStatus(userId.toString(), MembershipStatus.APPROVED))
                .thenReturn(Optional.empty());

        // Act
        missionProgressService.claimReward(userId, missionId);

        // Assert
        // Verify ledger was never touched
        verify(pointRecordingService, never()).recordPoints(anyString(), anyString(), anyDouble(), any(), anyString());

        // Ensure the individual progress was still saved properly even without a clan
        verify(progressRepository).save(argThat(progress -> progress.getIsClaimed() == true));
    }
}