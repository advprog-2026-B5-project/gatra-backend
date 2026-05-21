package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.model.*;
import id.ac.ui.cs.advprog.gatra.clan.repository.*;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanServiceImplTest {
    @Mock private ClanRepository clanRepository;
    @Mock private ClanMembershipRepository membershipRepository;
    @Mock private UserRepository userRepository;
    @Mock private BuffDebuffService buffDebuffService;


    @InjectMocks private ClanServiceImpl clanService;

    private Clan dummyClan;
    private String userId = "user-123";
    private String clanId = "clan-123";

    @BeforeEach
    void setUp() {
        dummyClan = Clan.builder().id(clanId).name("Test Clan").description("Desc").tier("BRONZE").build();
    }

    @Test
    void createClan_success() {
        CreateClanRequest req = new CreateClanRequest();
        req.setName("Test Clan");
        req.setDescription("Desc");

        when(membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.APPROVED)).thenReturn(false);
        when(membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.PENDING)).thenReturn(false);
        when(clanRepository.save(any(Clan.class))).thenReturn(dummyClan);
        when(membershipRepository.save(any(ClanMembership.class))).thenReturn(new ClanMembership());
        when(membershipRepository.countByClanIdAndStatus(any(), eq(MembershipStatus.APPROVED))).thenReturn(1L);
        when(buffDebuffService.buildCalculator(any())).thenReturn((cId, tier) -> 100.0);

        ClanResponse res = clanService.createClan(req, userId);

        assertNotNull(res);
        assertEquals("Test Clan", res.getName());
        verify(clanRepository, times(1)).save(any(Clan.class));
        verify(membershipRepository, times(1)).save(any(ClanMembership.class));
    }

    @Test
    void createClan_shouldThrowWhenUserAlreadyInClan() {
        CreateClanRequest req = new CreateClanRequest();
        when(membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.APPROVED)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> clanService.createClan(req, userId));
    }

    @Test
    void getClan_success() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(dummyClan));
        when(membershipRepository.countByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(5L);
        when(buffDebuffService.buildCalculator(any())).thenReturn((cId, tier) -> 100.0);

        ClanResponse res = clanService.getClan(clanId);

        assertNotNull(res);
        assertEquals(clanId, res.getId());
        assertEquals(5, res.getMemberCount());
        assertEquals(100.0, res.getScore());
    }

    @Test
    void deleteClan_success() {
        ClanMembership leaderMembership = ClanMembership.builder().role(ClanRole.LEADER).build();
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(dummyClan));
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.of(leaderMembership));

        clanService.deleteClan(clanId, userId);

        verify(clanRepository, times(1)).delete(dummyClan);
    }

    @Test
    void getMyClan_whenApproved() {
        ClanMembership membership = ClanMembership.builder()
                .clan(dummyClan)
                .userId(userId)
                .role(ClanRole.LEADER)
                .status(MembershipStatus.APPROVED)
                .build();

        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.APPROVED))
                .thenReturn(Optional.of(membership));

        when(buffDebuffService.buildCalculator(any())).thenReturn((cId, tier) -> 100.0);

        when(membershipRepository.findByClanIdAndStatus(clanId, MembershipStatus.APPROVED))
                .thenReturn(List.of(membership));

        when(membershipRepository.findByClanIdAndStatus(clanId, MembershipStatus.PENDING))
                .thenReturn(List.of());

        User dummyUser = new User();
        dummyUser.setDisplayName("Test User");
        when(userRepository.findByStringId(userId)).thenReturn(Optional.of(dummyUser));

        ClanResponse res = clanService.getMyClan(userId);

        assertNotNull(res);
        assertEquals(clanId, res.getId());
        assertEquals("LEADER", res.getMyRole());
    }

    @Test
    void kickMember_success() {
        String targetUserId = "target-123";
        ClanMembership leaderMembership = ClanMembership.builder().role(ClanRole.LEADER).build();
        ClanMembership targetMembership = ClanMembership.builder().userId(targetUserId).build();

        when(clanRepository.findById(clanId)).thenReturn(Optional.of(dummyClan));
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.of(leaderMembership));
        when(membershipRepository.findByClanIdAndUserId(clanId, targetUserId)).thenReturn(Optional.of(targetMembership));

        clanService.kickMember(clanId, targetUserId, userId);

        verify(membershipRepository, times(1)).delete(targetMembership);
    }
    @Test
    void createClan_shouldThrowWhenUserPending() {
        CreateClanRequest req = new CreateClanRequest();
        when(membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.APPROVED)).thenReturn(false);
        when(membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.PENDING)).thenReturn(true);
        assertThrows(RuntimeException.class, () -> clanService.createClan(req, userId));
    }

    @Test
    void getClan_throwsWhenNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.getClan(clanId));
    }

    @Test
    void deleteClan_throwsWhenNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.deleteClan(clanId, userId));
    }

    @Test
    void deleteClan_throwsWhenNotLeader() {
        ClanMembership mem = ClanMembership.builder().role(ClanRole.MEMBER).build();
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(dummyClan));
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.of(mem));
        assertThrows(RuntimeException.class, () -> clanService.deleteClan(clanId, userId));
    }

    @Test
    void getAllClans_success() {
        when(clanRepository.findAll()).thenReturn(List.of(dummyClan));
        when(membershipRepository.countByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(1L);
        when(buffDebuffService.buildCalculator(any())).thenReturn((cId, tier) -> 100.0);

        List<ClanResponse> list = clanService.getAllClans();
        assertEquals(1, list.size());
    }

    @Test
    void getMyClan_whenPending() {
        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.APPROVED)).thenReturn(Optional.empty());
        ClanMembership pending = ClanMembership.builder().clan(dummyClan).userId(userId).status(MembershipStatus.PENDING).build();
        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.PENDING)).thenReturn(Optional.of(pending));

        ClanResponse res = clanService.getMyClan(userId);
        assertNotNull(res);
        assertEquals(MembershipStatus.PENDING.name(), res.getMembershipStatus());
    }

    @Test
    void getMyClan_whenNull() {
        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.APPROVED)).thenReturn(Optional.empty());
        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.PENDING)).thenReturn(Optional.empty());
        assertNull(clanService.getMyClan(userId));
    }

    @Test
    void kickMember_throwsWhenClanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, "target", userId));
    }

    @Test
    void kickMember_throwsWhenNotLeader() {
        ClanMembership mem = ClanMembership.builder().role(ClanRole.MEMBER).build();
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(dummyClan));
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.of(mem));
        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, "target", userId));
    }

    @Test
    void kickMember_throwsWhenSelfKick() {
        ClanMembership mem = ClanMembership.builder().role(ClanRole.LEADER).build();
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(dummyClan));
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.of(mem));
        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, userId, userId));
    }

    @Test
    void kickMember_throwsWhenTargetNotMember() {
        ClanMembership mem = ClanMembership.builder().role(ClanRole.LEADER).build();
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(dummyClan));
        when(membershipRepository.findByClanIdAndUserId(clanId, userId)).thenReturn(Optional.of(mem));
        when(membershipRepository.findByClanIdAndUserId(clanId, "target")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, "target", userId));
    }
}
