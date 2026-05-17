package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.helper.MissionCompletionChecker;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.clan.strategy.BuffDebuffStrategy;
import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuffDebuffServiceImplTest {
    @Mock private ClanMembershipRepository membershipRepository;
    @Mock private MissionCompletionChecker missionCompletionChecker;
    @Mock private BuffDebuffStrategy strategy;

    private BuffDebuffServiceImpl buffDebuffService;

    @BeforeEach
    void setUp() {
        buffDebuffService = new BuffDebuffServiceImpl(membershipRepository, missionCompletionChecker, List.of(strategy));
    }

    @Test
    void getModifier_noMembers_returnsDefaultStrategyModifier() {
        String clanId = "clan1";
        when(membershipRepository.findByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(List.of());
        when(strategy.isApplicable(0.0)).thenReturn(true);
        ScoreModifier modifier = new ScoreModifier("NO_MEMBERS", 1.0);
        when(strategy.getModifier()).thenReturn(modifier);

        ScoreModifier res = buffDebuffService.getModifier(clanId);

        assertEquals(modifier, res);
    }

    @Test
    void getModifier_someMembersCompleted_calculatesRateAndReturnsStrategyModifier() {
        String clanId = "clan1";
        ClanMembership mem1 = ClanMembership.builder().userId("user1").build();
        ClanMembership mem2 = ClanMembership.builder().userId("user2").build();
        when(membershipRepository.findByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(List.of(mem1, mem2));
        
        when(missionCompletionChecker.hasCompletedAnyMission("user1")).thenReturn(true);
        when(missionCompletionChecker.hasCompletedAnyMission("user2")).thenReturn(false);
        
        when(strategy.isApplicable(0.5)).thenReturn(true);
        ScoreModifier modifier = new ScoreModifier("HALF_COMPLETED", 1.5);
        when(strategy.getModifier()).thenReturn(modifier);

        ScoreModifier res = buffDebuffService.getModifier(clanId);

        assertEquals(modifier, res);
    }
}
