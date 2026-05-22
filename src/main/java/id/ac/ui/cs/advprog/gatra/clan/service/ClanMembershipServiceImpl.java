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
    private final ClanValidator validator;

    @Override
    @Transactional
    public MembershipResponse applyToClan(String clanId, String userId) {
        validator.findClanOrThrow(clanId);
        validator.validateUserNotInAnyClan(userId);

        ClanMembership membership = ClanMembership.builder()
                .clan(clanRepository.getReferenceById(clanId))
                .userId(userId)
                .build();
        membershipRepository.save(membership);
        return toResponse(membership);
    }


    @Override
    @Transactional
    public MembershipResponse decideMembership(MembershipDecisionRequest request) {
       validator.validateLeader(request.getClanId(), request.getLeaderId());

        ClanMembership membership = membershipRepository
                .findByClanIdAndUserId(request.getClanId(), request.getApplicantId())
                .orElseThrow(() -> new IllegalArgumentException("Aplikasi tidak ditemukan."));

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
        validator.validateLeader(clanId, leaderId);
        return membershipRepository.findByClanIdAndStatus(clanId, MembershipStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void leaveClan(String clanId, String userId) {
        ClanMembership membership = membershipRepository.findByClanIdAndUserId(clanId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Kamu bukan anggota clan ini."));

        if (membership.getRole().equals(ClanRole.LEADER)) {
            throw new IllegalStateException("Ketua tidak bisa keluar clan.");
        }

        membershipRepository.delete(membership);
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
}
