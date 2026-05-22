package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.MembershipResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.*;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanResponseMapperTest {

    @Mock private ClanMembershipRepository membershipRepository;
    @Mock private UserRepository userRepository;
    @Mock private BuffDebuffService buffDebuffService;

    @InjectMocks private ClanResponseMapper mapper;

    private Clan clan;
    private ClanMembership leaderMembership;
    private ClanMembership memberMembership;

    @BeforeEach
    void setUp() {
        clan = Clan.builder()
                .id("clan1").name("Test Clan").description("Desc").tier("BRONZE").build();

        leaderMembership = ClanMembership.builder()
                .id("mem1").clan(clan).userId("leader1").role(ClanRole.LEADER)
                .status(MembershipStatus.APPROVED).build();

        memberMembership = ClanMembership.builder()
                .id("mem2").clan(clan).userId("user1").role(ClanRole.MEMBER)
                .status(MembershipStatus.APPROVED).build();
    }


    @Test
    void toSimpleResponse_returnsCorrectFields() {
        when(membershipRepository.findByClanIdAndStatus("clan1", MembershipStatus.APPROVED))
                .thenReturn(List.of(leaderMembership, memberMembership));
        when(userRepository.findByStringId(any())).thenReturn(Optional.empty());
        when(buffDebuffService.buildCalculator("clan1")).thenReturn((id, tier) -> 150.0);

        ClanResponse res = mapper.toSimpleResponse(clan);

        assertEquals("clan1", res.getId());
        assertEquals("Test Clan", res.getName());
        assertEquals("BRONZE", res.getTier());
        assertEquals(2, res.getMemberCount());
        assertEquals(150.0, res.getScore());
        assertNull(res.getMyRole());
        assertNull(res.getMembers());
    }

    @Test
    void toSimpleResponse_noMembers_memberCountZero() {
        when(membershipRepository.findByClanIdAndStatus("clan1", MembershipStatus.APPROVED))
                .thenReturn(List.of());
        when(buffDebuffService.buildCalculator("clan1")).thenReturn((id, tier) -> 0.0);

        ClanResponse res = mapper.toSimpleResponse(clan);

        assertEquals(0, res.getMemberCount());
        assertEquals(0.0, res.getScore());
    }

    @Test
    void toApprovedMemberResponse_leader_includesPendingApplications() {
        ClanMembership pending = ClanMembership.builder()
                .id("mem3").clan(clan).userId("applicant1").role(ClanRole.MEMBER)
                .status(MembershipStatus.PENDING).build();

        when(membershipRepository.findByClanIdAndStatus("clan1", MembershipStatus.APPROVED))
                .thenReturn(List.of(leaderMembership));
        when(membershipRepository.findByClanIdAndStatus("clan1", MembershipStatus.PENDING))
                .thenReturn(List.of(pending));
        when(userRepository.findByStringId(any())).thenReturn(Optional.empty());
        when(buffDebuffService.buildCalculator("clan1")).thenReturn((id, tier) -> 100.0);

        ClanResponse res = mapper.toApprovedMemberResponse(leaderMembership);

        assertEquals("LEADER", res.getMyRole());
        assertEquals(MembershipStatus.APPROVED.name(), res.getMembershipStatus());
        assertEquals(1, res.getMembers().size());
        assertEquals(1, res.getPendingApplications().size());
    }

    @Test
    void toApprovedMemberResponse_member_pendingApplicationsEmpty() {
        when(membershipRepository.findByClanIdAndStatus("clan1", MembershipStatus.APPROVED))
                .thenReturn(List.of(memberMembership));
        when(userRepository.findByStringId(any())).thenReturn(Optional.empty());
        when(buffDebuffService.buildCalculator("clan1")).thenReturn((id, tier) -> 100.0);

        ClanResponse res = mapper.toApprovedMemberResponse(memberMembership);

        assertEquals("MEMBER", res.getMyRole());
        assertTrue(res.getPendingApplications().isEmpty());
        verify(membershipRepository, never())
                .findByClanIdAndStatus("clan1", MembershipStatus.PENDING);
    }

    // ─── toPendingResponse ────────────────────────────────────────────────────

    @Test
    void toPendingResponse_returnsMinimalFields() {
        ClanMembership pending = ClanMembership.builder()
                .id("mem3").clan(clan).userId("user2")
                .status(MembershipStatus.PENDING).build();

        ClanResponse res = mapper.toPendingResponse(pending);

        assertEquals("clan1", res.getId());
        assertEquals("Test Clan", res.getName());
        assertEquals(MembershipStatus.PENDING.name(), res.getMembershipStatus());
    }

    @Test
    void toMembershipResponse_userFound_setsDisplayName() {
        User user = new User();
        user.setDisplayName("Budi");
        when(userRepository.findByStringId("user1")).thenReturn(Optional.of(user));

        MembershipResponse res = mapper.toMembershipResponse(memberMembership);

        assertEquals("mem2", res.getId());
        assertEquals("user1", res.getUserId());
        assertEquals("clan1", res.getClanId());
        assertEquals("Budi", res.getDisplayName());
        assertEquals(ClanRole.MEMBER, res.getRole());
        assertEquals(MembershipStatus.APPROVED, res.getStatus());
    }

    @Test
    void toMembershipResponse_userNotFound_displayNameUnknown() {
        when(userRepository.findByStringId("user1")).thenReturn(Optional.empty());

        MembershipResponse res = mapper.toMembershipResponse(memberMembership);

        assertEquals("Unknown", res.getDisplayName());
    }
}