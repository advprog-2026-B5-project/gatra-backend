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
class ClanServiceImplTest {

    @Mock private ClanRepository clanRepository;
    @Mock private ClanMembershipRepository membershipRepository;
    @Mock private ClanResponseMapper responseMapper;
    @Mock private ClanValidator validator;

    @InjectMocks private ClanServiceImpl clanService;

    private Clan dummyClan;
    private final String userId = "user-123";
    private final String clanId = "clan-123";

    @BeforeEach
    void setUp() {
        dummyClan = Clan.builder().id(clanId).name("Test Clan").description("Desc").tier("BRONZE").build();
    }

    @Test
    void createClan_success() {
        CreateClanRequest req = new CreateClanRequest();
        req.setName("Test Clan");
        req.setDescription("Desc");

        ClanResponse expected = ClanResponse.builder().id(clanId).name("Test Clan").build();

        doNothing().when(validator).validateUserNotInAnyClan(userId);
        when(clanRepository.save(any(Clan.class))).thenReturn(dummyClan);
        when(membershipRepository.save(any(ClanMembership.class))).thenReturn(new ClanMembership());
        when(responseMapper.toSimpleResponse(any(Clan.class))).thenReturn(expected);

        ClanResponse res = clanService.createClan(req, userId);

        assertNotNull(res);
        assertEquals("Test Clan", res.getName());
        verify(clanRepository).save(any(Clan.class));
        verify(membershipRepository).save(any(ClanMembership.class));
        verify(responseMapper).toSimpleResponse(any(Clan.class));
    }

    @Test
    void createClan_userAlreadyInClan_throws() {
        doThrow(new RuntimeException("Sudah dalam clan."))
                .when(validator).validateUserNotInAnyClan(userId);

        assertThrows(RuntimeException.class, () -> clanService.createClan(new CreateClanRequest(), userId));
        verify(clanRepository, never()).save(any());
    }

    @Test
    void getClan_success() {
        ClanResponse expected = ClanResponse.builder().id(clanId).memberCount(5).score(100.0).build();

        when(validator.findClanOrThrow(clanId)).thenReturn(dummyClan);
        when(responseMapper.toSimpleResponse(dummyClan)).thenReturn(expected);

        ClanResponse res = clanService.getClan(clanId);

        assertNotNull(res);
        assertEquals(clanId, res.getId());
        assertEquals(5, res.getMemberCount());
        assertEquals(100.0, res.getScore());
    }

    @Test
    void getClan_notFound_throws() {
        doThrow(new RuntimeException("Clan tidak ditemukan."))
                .when(validator).findClanOrThrow(clanId);

        assertThrows(RuntimeException.class, () -> clanService.getClan(clanId));
    }


    @Test
    void deleteClan_success() {
        when(validator.findClanOrThrow(clanId)).thenReturn(dummyClan);
        doNothing().when(validator).validateLeader(clanId, userId);

        clanService.deleteClan(clanId, userId);

        verify(clanRepository).delete(dummyClan);
    }

    @Test
    void deleteClan_notFound_throws() {
        doThrow(new RuntimeException("Clan tidak ditemukan."))
                .when(validator).findClanOrThrow(clanId);

        assertThrows(RuntimeException.class, () -> clanService.deleteClan(clanId, userId));
        verify(clanRepository, never()).delete(any());
    }

    @Test
    void deleteClan_notLeader_throws() {
        when(validator.findClanOrThrow(clanId)).thenReturn(dummyClan);
        doThrow(new RuntimeException("Bukan ketua clan."))
                .when(validator).validateLeader(clanId, userId);

        assertThrows(RuntimeException.class, () -> clanService.deleteClan(clanId, userId));
        verify(clanRepository, never()).delete(any());
    }

    @Test
    void getAllClans_success() {
        ClanResponse expected = ClanResponse.builder().id(clanId).build();
        when(clanRepository.findAll()).thenReturn(List.of(dummyClan));
        when(responseMapper.toSimpleResponse(dummyClan)).thenReturn(expected);

        List<ClanResponse> res = clanService.getAllClans();

        assertEquals(1, res.size());
        assertEquals(clanId, res.get(0).getId());
    }

    @Test
    void getAllClans_empty_returnsEmptyList() {
        when(clanRepository.findAll()).thenReturn(List.of());

        List<ClanResponse> res = clanService.getAllClans();

        assertTrue(res.isEmpty());
        verify(responseMapper, never()).toSimpleResponse(any());
    }


    @Test
    void getMyClan_whenApproved() {
        ClanMembership membership = ClanMembership.builder()
                .clan(dummyClan).userId(userId).role(ClanRole.LEADER)
                .status(MembershipStatus.APPROVED).build();
        ClanResponse expected = ClanResponse.builder().id(clanId).myRole("LEADER").build();

        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.APPROVED))
                .thenReturn(Optional.of(membership));
        when(responseMapper.toApprovedMemberResponse(membership)).thenReturn(expected);

        ClanResponse res = clanService.getMyClan(userId);

        assertNotNull(res);
        assertEquals(clanId, res.getId());
        assertEquals("LEADER", res.getMyRole());
        verify(responseMapper).toApprovedMemberResponse(membership);
    }

    @Test
    void getMyClan_whenPending() {
        ClanMembership pending = ClanMembership.builder()
                .clan(dummyClan).userId(userId).status(MembershipStatus.PENDING).build();
        ClanResponse expected = ClanResponse.builder().id(clanId)
                .membershipStatus(MembershipStatus.PENDING.name()).build();

        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.APPROVED))
                .thenReturn(Optional.empty());
        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.PENDING))
                .thenReturn(Optional.of(pending));
        when(responseMapper.toPendingResponse(pending)).thenReturn(expected);

        ClanResponse res = clanService.getMyClan(userId);

        assertNotNull(res);
        assertEquals(MembershipStatus.PENDING.name(), res.getMembershipStatus());
        verify(responseMapper).toPendingResponse(pending);
    }

    @Test
    void getMyClan_whenNull() {
        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.APPROVED))
                .thenReturn(Optional.empty());
        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.PENDING))
                .thenReturn(Optional.empty());

        assertNull(clanService.getMyClan(userId));
        verify(responseMapper, never()).toApprovedMemberResponse(any());
        verify(responseMapper, never()).toPendingResponse(any());
    }

    @Test
    void kickMember_success() {
        String targetUserId = "target-123";
        ClanMembership targetMembership = ClanMembership.builder().userId(targetUserId).build();

        when(validator.findClanOrThrow(clanId)).thenReturn(dummyClan);
        doNothing().when(validator).validateLeader(clanId, userId);
        doNothing().when(validator).validateNotSelfKick(userId, targetUserId);
        when(membershipRepository.findByClanIdAndUserId(clanId, targetUserId))
                .thenReturn(Optional.of(targetMembership));

        clanService.kickMember(clanId, targetUserId, userId);

        verify(membershipRepository).delete(targetMembership);
    }

    @Test
    void kickMember_clanNotFound_throws() {
        doThrow(new RuntimeException("Clan tidak ditemukan."))
                .when(validator).findClanOrThrow(clanId);

        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, "target", userId));
        verify(membershipRepository, never()).delete(any());
    }

    @Test
    void kickMember_notLeader_throws() {
        when(validator.findClanOrThrow(clanId)).thenReturn(dummyClan);
        doThrow(new RuntimeException("Bukan ketua clan."))
                .when(validator).validateLeader(clanId, userId);

        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, "target", userId));
        verify(membershipRepository, never()).delete(any());
    }

    @Test
    void kickMember_selfKick_throws() {
        when(validator.findClanOrThrow(clanId)).thenReturn(dummyClan);
        doNothing().when(validator).validateLeader(clanId, userId);
        doThrow(new RuntimeException("Tidak bisa kick diri sendiri."))
                .when(validator).validateNotSelfKick(userId, userId);

        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, userId, userId));
        verify(membershipRepository, never()).delete(any());
    }

    @Test
    void kickMember_targetNotMember_throws() {
        String targetUserId = "target-123";

        when(validator.findClanOrThrow(clanId)).thenReturn(dummyClan);
        doNothing().when(validator).validateLeader(clanId, userId);
        doNothing().when(validator).validateNotSelfKick(userId, targetUserId);
        when(membershipRepository.findByClanIdAndUserId(clanId, targetUserId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, targetUserId, userId));
    }
}