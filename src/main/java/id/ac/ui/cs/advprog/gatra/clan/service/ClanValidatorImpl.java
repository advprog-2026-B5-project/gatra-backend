package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanRole;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClanValidatorImpl implements ClanValidator {

    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;

    @Override
    public Clan findClanOrThrow(String clanId) {
        return clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan dengan id " + clanId + " tidak ditemukan"));
    }

    @Override
    public void validateLeader(String clanId, String userId) {
        membershipRepository.findByClanIdAndUserId(clanId, userId)
                .filter(m -> m.getRole() == ClanRole.LEADER)
                .orElseThrow(() -> new RuntimeException("Hanya ketua clan yang dapat melakukan aksi ini"));
    }

    @Override
    public void validateUserNotInAnyClan(String userId) {
        if (membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.APPROVED) ||
                membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.PENDING)) {
            throw new RuntimeException("User sudah terdaftar atau memiliki pending di sebuah clan.");
        }
    }

    @Override
    public void validateNotSelfKick(String leaderId, String targetUserId) {
        if (leaderId.equals(targetUserId)) {
            throw new RuntimeException("Ketua tidak bisa mengeluarkan diri sendiri melalui fitur kick.");
        }
    }
}