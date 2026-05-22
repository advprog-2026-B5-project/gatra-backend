package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanRole;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanValidatorImplTest {

    @Mock
    private ClanRepository clanRepository;

    @Mock
    private ClanMembershipRepository membershipRepository;

    @InjectMocks
    private ClanValidatorImpl clanValidator;

    @Test
    void findClanOrThrow_clanExists_returnsClan() {
        Clan clan = Clan.builder().id("c1").build();
        when(clanRepository.findById("c1")).thenReturn(Optional.of(clan));

        Clan result = clanValidator.findClanOrThrow("c1");

        assertNotNull(result);
        assertEquals("c1", result.getId());
        verify(clanRepository).findById("c1");
    }

    @Test
    void findClanOrThrow_clanNotFound_throwsException() {
        when(clanRepository.findById("c1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> clanValidator.findClanOrThrow("c1"));

        assertEquals("Clan dengan id c1 tidak ditemukan", exception.getMessage());
        verify(clanRepository).findById("c1");
    }

    @Test
    void validateLeader_isLeader_doesNotThrow() {
        ClanMembership membership = ClanMembership.builder()
                .userId("u1")
                .role(ClanRole.LEADER)
                .build();

        when(membershipRepository.findByClanIdAndUserId("c1", "u1"))
                .thenReturn(Optional.of(membership));

        assertDoesNotThrow(() -> clanValidator.validateLeader("c1", "u1"));
        verify(membershipRepository).findByClanIdAndUserId("c1", "u1");
    }

    @Test
    void validateLeader_isNotLeader_throwsException() {
        ClanMembership membership = ClanMembership.builder()
                .userId("u1")
                .role(ClanRole.MEMBER)
                .build();

        when(membershipRepository.findByClanIdAndUserId("c1", "u1"))
                .thenReturn(Optional.of(membership));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> clanValidator.validateLeader("c1", "u1"));

        assertEquals("Hanya ketua clan yang dapat melakukan aksi ini", exception.getMessage());
    }

    @Test
    void validateLeader_membershipNotFound_throwsException() {
        when(membershipRepository.findByClanIdAndUserId("c1", "u1"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> clanValidator.validateLeader("c1", "u1"));

        assertEquals("Hanya ketua clan yang dapat melakukan aksi ini", exception.getMessage());
    }


    @Test
    void validateUserNotInAnyClan_notInAnyClan_doesNotThrow() {
        when(membershipRepository.existsByUserIdAndStatus("u1", MembershipStatus.APPROVED))
                .thenReturn(false);
        when(membershipRepository.existsByUserIdAndStatus("u1", MembershipStatus.PENDING))
                .thenReturn(false);

        assertDoesNotThrow(() -> clanValidator.validateUserNotInAnyClan("u1"));
    }

    @Test
    void validateUserNotInAnyClan_statusApproved_throwsException() {
        when(membershipRepository.existsByUserIdAndStatus("u1", MembershipStatus.APPROVED))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> clanValidator.validateUserNotInAnyClan("u1"));

        assertEquals("User sudah terdaftar atau memiliki pending di sebuah clan.", exception.getMessage());

        verify(membershipRepository, never()).existsByUserIdAndStatus("u1", MembershipStatus.PENDING);
    }

    @Test
    void validateUserNotInAnyClan_statusPending_throwsException() {
        when(membershipRepository.existsByUserIdAndStatus("u1", MembershipStatus.APPROVED))
                .thenReturn(false);
        when(membershipRepository.existsByUserIdAndStatus("u1", MembershipStatus.PENDING))
                .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> clanValidator.validateUserNotInAnyClan("u1"));

        assertEquals("User sudah terdaftar atau memiliki pending di sebuah clan.", exception.getMessage());
    }


    @Test
    void validateNotSelfKick_differentUsers_doesNotThrow() {
        assertDoesNotThrow(() -> clanValidator.validateNotSelfKick("leader1", "target2"));
    }

    @Test
    void validateNotSelfKick_sameUser_throwsException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> clanValidator.validateNotSelfKick("u1", "u1"));

        assertEquals("Ketua tidak bisa mengeluarkan diri sendiri melalui fitur kick.", exception.getMessage());
    }
}