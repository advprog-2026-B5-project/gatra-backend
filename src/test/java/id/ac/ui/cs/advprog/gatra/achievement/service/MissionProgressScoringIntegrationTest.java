package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.event.MissionRewardClaimedEvent;
import id.ac.ui.cs.advprog.gatra.achievement.listener.MissionRewardPointsListener;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionProgressScoringIntegrationTest {

    @Mock private ClanMembershipRepository clanMembershipRepository;
    @Mock private PointRecordingService pointRecordingService;

    @InjectMocks
    private MissionRewardPointsListener listener;

    private UUID userId;
    private UUID missionId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        missionId = UUID.randomUUID();
    }

    @Test
    void onMissionRewardClaimed_userInClan_shouldRecordPoints() {
        Clan mockClan = new Clan();
        mockClan.setId("clan-999");

        ClanMembership mockMembership = new ClanMembership();
        mockMembership.setClan(mockClan);

        when(clanMembershipRepository.findFirstByUserIdAndStatus(userId.toString(), MembershipStatus.APPROVED))
                .thenReturn(Optional.of(mockMembership));

        MissionRewardClaimedEvent event = new MissionRewardClaimedEvent(userId, missionId, 50);

        listener.onMissionRewardClaimed(event);

        verify(pointRecordingService, times(1)).recordPoints(
                userId.toString(),
                "clan-999",
                50.0,
                PointActivityType.DAILY_MISSION_COMPLETED,
                missionId.toString()
        );
    }

    @Test
    void onMissionRewardClaimed_userNotInClan_shouldNotRecordPoints() {
        when(clanMembershipRepository.findFirstByUserIdAndStatus(userId.toString(), MembershipStatus.APPROVED))
                .thenReturn(Optional.empty());

        MissionRewardClaimedEvent event = new MissionRewardClaimedEvent(userId, missionId, 50);

        listener.onMissionRewardClaimed(event);

        verify(pointRecordingService, never()).recordPoints(anyString(), anyString(), anyDouble(), any(), anyString());
    }

    @Test
    void onMissionRewardClaimed_zeroRewardPoints_shouldSkipEntirely() {
        MissionRewardClaimedEvent event = new MissionRewardClaimedEvent(userId, missionId, 0);

        listener.onMissionRewardClaimed(event);

        verify(clanMembershipRepository, never()).findFirstByUserIdAndStatus(anyString(), any());
        verify(pointRecordingService, never()).recordPoints(anyString(), anyString(), anyDouble(), any(), anyString());
    }

    @Test
    void onMissionRewardClaimed_negativeRewardPoints_shouldSkipEntirely() {
        MissionRewardClaimedEvent event = new MissionRewardClaimedEvent(userId, missionId, -10);

        listener.onMissionRewardClaimed(event);

        verify(clanMembershipRepository, never()).findFirstByUserIdAndStatus(anyString(), any());
        verify(pointRecordingService, never()).recordPoints(anyString(), anyString(), anyDouble(), any(), anyString());
    }
}