package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.model.*;
import id.ac.ui.cs.advprog.gatra.clan.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ClanMembershipServiceImpl implements ClanMembershipService{
    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;

    @Override
    @Transactional
    public MembershipResponse applyToClan(String clanId, String userId) {
        clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan dengan id " + clanId + " tidak ditemukan."));

        if (membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.APPROVED) ||
                membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.PENDING)) {
            throw new RuntimeException("User sudah terdaftar atau memiliki pending di sebuah clan.");
        }

        ClanMembership membership = ClanMembership.builder()
                .clan(clanRepository.getReferenceById(clanId))
                .userId(userId)
                .build();
        membershipRepository.save(membership);

        return toResponse(membership);
    }

    private MembershipResponse toResponse(ClanMembership m) {
        return MembershipResponse.builder()
                .id(m.getId())
                .clanId(m.getClan().getId())
                .userId(m.getUserId())
                .role(m.getRole())
                .status(m.getStatus())
                .build();
    }

    @Override
    @Transactional
    public MembershipResponse decideMembership(String clanId, String applicantId,
                                               MembershipDecisionRequest request, String leaderId) {
        // validasi leader
        membershipRepository.findByClanIdAndUserId(clanId, leaderId)
                .filter(m -> m.getRole() == ClanRole.LEADER)
                .orElseThrow(() -> new RuntimeException("Hanya ketua clan yang bisa melakukan aksi ini"));

        ClanMembership membership = membershipRepository
                .findByClanIdAndUserId(clanId, applicantId)
                .orElseThrow(() -> new RuntimeException("Aplikasi tidak ditemukan."));

        // state Pattern untuk transisi
        if (request.getDecision() == MembershipStatus.APPROVED) {
            membership.approve();
        } else {
            membership.reject();
        }

        membershipRepository.save(membership);
        return toResponse(membership);
    }

    @Override
    public List<MembershipResponse> getPendingApplications(String clanId, String leaderId) {
        membershipRepository.findByClanIdAndUserId(clanId, leaderId)
                .filter(m -> m.getRole() == ClanRole.LEADER)
                .orElseThrow(() -> new RuntimeException("Hanya ketua clan yang bisa melakukan aksi ini"));

        return membershipRepository.findByClanIdAndStatus(clanId, MembershipStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void leaveClan(String clanId, String userId) {
        ClanMembership membership = membershipRepository.findByClanIdAndUserId(clanId, userId)
                .orElseThrow(() -> new RuntimeException("Kamu bukan anggota clan ini."));

        if (membership.getRole().equals(ClanRole.LEADER)) {
            throw new RuntimeException("Ketua tidak bisa keluar clan.");
        }

        membershipRepository.delete(membership);
    }
}
