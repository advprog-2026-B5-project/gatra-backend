package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.model.*;
import id.ac.ui.cs.advprog.gatra.clan.repository.*;
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
    @Mock private ClanValidator validator;

    @InjectMocks private ClanMembershipServiceImpl membershipService;

    @Test
    void applyToClan_success() {
        Clan clan = Clan.builder().id("clan1").build();
        when(validator.findClanOrThrow("clan1")).thenReturn(clan);
        doNothing().when(validator).validateUserNotInAnyClan("user1");
        when(clanRepository.getReferenceById("clan1")).thenReturn(clan);
        when(membershipRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MembershipResponse res = membershipService.applyToClan("clan1", "user1");

        assertNotNull(res);
        assertEquals("user1", res.getUserId());
        assertEquals("clan1", res.getClanId());
        verify(membershipRepository).save(any());
    }

    @Test
    void applyToClan_clanNotFound_throws() {
        doThrow(new RuntimeException("Clan tidak ditemukan."))
                .when(validator).findClanOrThrow("clan1");

        assertThrows(RuntimeException.class, () -> membershipService.applyToClan("clan1", "user1"));
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void applyToClan_userAlreadyInClan_throws() {
        when(validator.findClanOrThrow("clan1")).thenReturn(Clan.builder().id("clan1").build());
        doThrow(new RuntimeException("Sudah dalam clan."))
                .when(validator).validateUserNotInAnyClan("user1");

        assertThrows(RuntimeException.class, () -> membershipService.applyToClan("clan1", "user1"));
        verify(membershipRepository, never()).save(any());
    }


    @Test
    void decideMembership_approve_success() {
        ClanMembership membership = pendingMembership("mem1", "user1"); // state sudah PendingState dari builder

        doNothing().when(validator).validateLeader("clan1", "leader1");
        when(membershipRepository.findByClanIdAndUserId("clan1", "user1"))
                .thenReturn(Optional.of(membership));
        when(membershipRepository.save(any())).thenReturn(membership);

        MembershipResponse res = membershipService.decideMembership(
                decisionReq("clan1", "user1", "leader1", MembershipStatus.APPROVED));

        assertEquals(MembershipStatus.APPROVED, res.getStatus());
        verify(membershipRepository).save(membership);
    }

    @Test
    void decideMembership_reject_success() {
        ClanMembership membership = pendingMembership("mem1", "user1");

        doNothing().when(validator).validateLeader("clan1", "leader1");
        when(membershipRepository.findByClanIdAndUserId("clan1", "user1"))
                .thenReturn(Optional.of(membership));
        when(membershipRepository.save(any())).thenReturn(membership);

        MembershipResponse res = membershipService.decideMembership(
                decisionReq("clan1", "user1", "leader1", MembershipStatus.REJECTED));

        assertEquals(MembershipStatus.REJECTED, res.getStatus());
        verify(membershipRepository).save(membership);
    }

    @Test
    void decideMembership_notLeader_throws() {
        doThrow(new RuntimeException("Bukan ketua clan."))
                .when(validator).validateLeader("clan1", "notLeader");

        MembershipDecisionRequest req = decisionReq("clan1", "user1", "notLeader", MembershipStatus.APPROVED);
        assertThrows(RuntimeException.class, () -> membershipService.decideMembership(req));
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void decideMembership_applicationNotFound_throws() {
        doNothing().when(validator).validateLeader("clan1", "leader1");
        when(membershipRepository.findByClanIdAndUserId("clan1", "user1"))
                .thenReturn(Optional.empty());

        MembershipDecisionRequest req = decisionReq("clan1", "user1", "leader1", MembershipStatus.APPROVED);
        assertThrows(RuntimeException.class, () -> membershipService.decideMembership(req));
    }

    // ─── getPendingApplications ───────────────────────────────────────────────

    @Test
    void getPendingApplications_success() {
        doNothing().when(validator).validateLeader("clan1", "leader1");
        when(membershipRepository.findByClanIdAndStatus("clan1", MembershipStatus.PENDING))
                .thenReturn(List.of(pendingMembership("mem1", "user1")));

        List<MembershipResponse> res = membershipService.getPendingApplications("clan1", "leader1");

        assertEquals(1, res.size());
        assertEquals("user1", res.get(0).getUserId());
        assertEquals(MembershipStatus.PENDING, res.get(0).getStatus());
    }

    @Test
    void getPendingApplications_notLeader_throws() {
        doThrow(new RuntimeException("Bukan ketua clan."))
                .when(validator).validateLeader("clan1", "notLeader");

        assertThrows(RuntimeException.class,
                () -> membershipService.getPendingApplications("clan1", "notLeader"));
    }

    // ─── leaveClan ────────────────────────────────────────────────────────────

    @Test
    void leaveClan_success() {
        ClanMembership mem = ClanMembership.builder()
                .id("mem1").userId("user1").role(ClanRole.MEMBER).build();
        when(membershipRepository.findByClanIdAndUserId("clan1", "user1"))
                .thenReturn(Optional.of(mem));

        membershipService.leaveClan("clan1", "user1");

        verify(membershipRepository).delete(mem);
    }

    @Test
    void leaveClan_leader_throws() {
        ClanMembership mem = ClanMembership.builder()
                .id("mem1").userId("leader1").role(ClanRole.LEADER).build();
        when(membershipRepository.findByClanIdAndUserId("clan1", "leader1"))
                .thenReturn(Optional.of(mem));

        assertThrows(RuntimeException.class, () -> membershipService.leaveClan("clan1", "leader1"));
        verify(membershipRepository, never()).delete(any());
    }

    @Test
    void leaveClan_notMember_throws() {
        when(membershipRepository.findByClanIdAndUserId("clan1", "user1"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> membershipService.leaveClan("clan1", "user1"));
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private ClanMembership pendingMembership(String id, String userId) {
        return ClanMembership.builder()
                .id(id)
                .clan(Clan.builder().id("clan1").build())
                .userId(userId)
                .role(ClanRole.MEMBER)
                .status(MembershipStatus.PENDING)
                .build();
    }

    private MembershipDecisionRequest decisionReq(
            String clanId, String applicantId, String leaderId, MembershipStatus decision) {
        MembershipDecisionRequest req = new MembershipDecisionRequest();
        req.setClanId(clanId);
        req.setApplicantId(applicantId);
        req.setLeaderId(leaderId);
        req.setDecision(decision);
        return req;
    }
}