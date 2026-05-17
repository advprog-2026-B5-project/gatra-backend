package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.model.*;
import id.ac.ui.cs.advprog.gatra.clan.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanMembershipServiceImplTest {
    @Mock private ClanRepository clanRepository;
    @Mock private ClanMembershipRepository membershipRepository;

    @InjectMocks private ClanMembershipServiceImpl membershipService;

    private Clan dummyClan;
    private String clanId = "clan-123";
    private String userId = "user-123";
    private String leaderId = "leader-123";

    @BeforeEach
    void setUp() {
        dummyClan = Clan.builder().id(clanId).name("Test Clan").build();
    }

    @Test
    void applyToClan_success() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(dummyClan));
        when(membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.APPROVED)).thenReturn(false);
        when(membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.PENDING)).thenReturn(false);
        when(clanRepository.getReferenceById(clanId)).thenReturn(dummyClan);
        
        ClanMembership mockSaved = ClanMembership.builder().id("mem-1").clan(dummyClan).userId(userId).status(MembershipStatus.PENDING).role(ClanRole.MEMBER).build();
        
        when(membershipRepository.save(any(ClanMembership.class))).thenAnswer(i -> {
            ClanMembership m = i.getArgument(0);
            m.setId("mem-1");
            return m;
        });

        MembershipResponse res = membershipService.applyToClan(clanId, userId);

        assertNotNull(res);
        verify(membershipRepository, times(1)).save(any(ClanMembership.class));
    }

    @Test
    void decideMembership_approve() {
        MembershipDecisionRequest req = new MembershipDecisionRequest();
        req.setDecision(MembershipStatus.APPROVED);

        ClanMembership leaderMembership = ClanMembership.builder().role(ClanRole.LEADER).build();
        ClanMembership applicantMembership = ClanMembership.builder().clan(dummyClan).userId(userId).status(MembershipStatus.PENDING).build();

        when(membershipRepository.findByClanIdAndUserId(clanId, leaderId)).thenReturn(Optional.of(leaderMembership));
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.of(applicantMembership));
        when(membershipRepository.save(any(ClanMembership.class))).thenReturn(applicantMembership);

        MembershipResponse res = membershipService.decideMembership(clanId, userId, req, leaderId);

        assertEquals(MembershipStatus.APPROVED, res.getStatus());
        verify(membershipRepository, times(1)).save(applicantMembership);
    }

    @Test
    void getPendingApplications_success() {
        ClanMembership leaderMembership = ClanMembership.builder().role(ClanRole.LEADER).build();
        ClanMembership applicantMembership = ClanMembership.builder().clan(dummyClan).userId(userId).status(MembershipStatus.PENDING).build();

        when(membershipRepository.findByClanIdAndUserId(clanId, leaderId)).thenReturn(Optional.of(leaderMembership));
        when(membershipRepository.findByClanIdAndStatus(clanId, MembershipStatus.PENDING)).thenReturn(List.of(applicantMembership));

        List<MembershipResponse> res = membershipService.getPendingApplications(clanId, leaderId);

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
    }

    @Test
    void leaveClan_success() {
        ClanMembership membership = ClanMembership.builder().clan(dummyClan).userId(userId).role(ClanRole.MEMBER).status(MembershipStatus.APPROVED).build();
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.of(membership));

        membershipService.leaveClan(clanId, userId);

        verify(membershipRepository, times(1)).delete(membership);
    }
    @Test
    void applyToClan_clanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> membershipService.applyToClan(clanId, userId));
    }

    @Test
    void applyToClan_alreadyMember() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(dummyClan));
        when(membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.APPROVED)).thenReturn(true);
        assertThrows(RuntimeException.class, () -> membershipService.applyToClan(clanId, userId));
    }

    @Test
    void decideMembership_reject() {
        MembershipDecisionRequest req = new MembershipDecisionRequest();
        req.setDecision(MembershipStatus.REJECTED);

        ClanMembership leaderMembership = ClanMembership.builder().role(ClanRole.LEADER).build();
        ClanMembership applicantMembership = ClanMembership.builder().clan(dummyClan).userId(userId).status(MembershipStatus.PENDING).build();

        when(membershipRepository.findByClanIdAndUserId(clanId, leaderId)).thenReturn(Optional.of(leaderMembership));
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.of(applicantMembership));
        when(membershipRepository.save(any(ClanMembership.class))).thenReturn(applicantMembership);

        MembershipResponse res = membershipService.decideMembership(clanId, userId, req, leaderId);
        assertEquals(MembershipStatus.REJECTED, res.getStatus());
    }

    @Test
    void decideMembership_leaderValidationFails() {
        ClanMembership notLeader = ClanMembership.builder().role(ClanRole.MEMBER).build();
        when(membershipRepository.findByClanIdAndUserId(clanId, leaderId)).thenReturn(Optional.of(notLeader));
        
        assertThrows(RuntimeException.class, () -> membershipService.decideMembership(clanId, userId, new MembershipDecisionRequest(), leaderId));
    }

    @Test
    void decideMembership_applicantNotFound() {
        ClanMembership leaderMembership = ClanMembership.builder().role(ClanRole.LEADER).build();
        when(membershipRepository.findByClanIdAndUserId(clanId, leaderId)).thenReturn(Optional.of(leaderMembership));
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> membershipService.decideMembership(clanId, userId, new MembershipDecisionRequest(), leaderId));
    }

    @Test
    void getPendingApplications_leaderValidationFails() {
        ClanMembership notLeader = ClanMembership.builder().role(ClanRole.MEMBER).build();
        when(membershipRepository.findByClanIdAndUserId(clanId, leaderId)).thenReturn(Optional.of(notLeader));
        
        assertThrows(RuntimeException.class, () -> membershipService.getPendingApplications(clanId, leaderId));
    }

    @Test
    void leaveClan_notMember() {
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> membershipService.leaveClan(clanId, userId));
    }

    @Test
    void leaveClan_leaderCannotLeave() {
        ClanMembership leaderMembership = ClanMembership.builder().role(ClanRole.LEADER).build();
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.of(leaderMembership));
        
        assertThrows(RuntimeException.class, () -> membershipService.leaveClan(clanId, userId));
    }
}
