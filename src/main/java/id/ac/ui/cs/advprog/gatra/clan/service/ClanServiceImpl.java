package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.model.*;
import id.ac.ui.cs.advprog.gatra.clan.repository.*;

import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import id.ac.ui.cs.advprog.gatra.scoring.service.ClanScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClanServiceImpl implements ClanService {
    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final ClanScoringService clanScoringService;
    private final BuffDebuffService buffDebuffService;

    @Override
    @Transactional
    public ClanResponse createClan(CreateClanRequest request, String userId){
        if(membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.APPROVED) ||
            membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.PENDING)){
            throw new RuntimeException("User sudah terdaftar atau memiliki pending di sebuah clan.");
        }

        Clan clan=Clan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        clanRepository.save(clan);

        ClanMembership membership = ClanMembership.builder()
                .clan(clan)
                .userId(userId)
                .role(ClanRole.LEADER)
                .status(MembershipStatus.APPROVED)
                .build();
        membershipRepository.save(membership);

        return toResponse(clan);
    }

    private ClanResponse toResponse(Clan clan){
        long memberCount = membershipRepository
                .countByClanIdAndStatus(clan.getId(), MembershipStatus.APPROVED);

        String currentClanTier = clan.getTier();

        ScoreModifier modifier = buffDebuffService.getModifier(clan.getId());
        double finalScore = clanScoringService.calculateClanScore(
                clan.getId(), currentClanTier, List.of(modifier)
        );

        return ClanResponse.builder()
                .id(clan.getId())
                .name(clan.getName())
                .description(clan.getDescription())
                .createdAt(clan.getCreatedAt())
                .memberCount((int) memberCount)
                .score(finalScore)
                .tier(currentClanTier)
                .build();
    }

    @Override
    public ClanResponse getClan(String clanId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan dengan id " + clanId + " tidak ditemukan"));
        return toResponse(clan);
    }

    @Override
    @Transactional
    public void deleteClan(String clanId, String userId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan dengan id " + clanId + " tidak ditemukan"));

        // hanya leader yg bisa hapus
        membershipRepository.findByClanIdAndUserId(clanId, userId)
                .filter(m -> m.getRole() == ClanRole.LEADER)
                .orElseThrow(() -> new RuntimeException("Hanya ketua clan yang dapat menghapus clan"));

        clanRepository.delete(clan);
    }

    @Override
    public List<ClanResponse> getAllClans() {
        return clanRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ClanResponse getMyClan(String userId) {
        Optional<ClanMembership> approved = membershipRepository
                .findByUserIdAndStatus(userId, MembershipStatus.APPROVED);

        if (approved.isPresent()) {
            ClanMembership m = approved.get();
            Clan clan = m.getClan();

            // TODO: Replace this hardcoded tier with actual tier fetched from the Leaderboard module
            String currentClanTier = "BRONZE";
            double finalScore = clanScoringService.calculateClanScore(clan.getId(), currentClanTier, List.of());

            List<MembershipResponse> members = membershipRepository
                    .findByClanIdAndStatus(clan.getId(), MembershipStatus.APPROVED)
                    .stream().map(this::toMembershipResponse).toList();

            List<MembershipResponse> pending = new ArrayList<>();
            if (m.getRole() == ClanRole.LEADER) {
                pending = membershipRepository
                        .findByClanIdAndStatus(clan.getId(), MembershipStatus.PENDING)
                        .stream().map(this::toMembershipResponse).toList();
            }

            return ClanResponse.builder()
                    .id(clan.getId())
                    .name(clan.getName())
                    .description(clan.getDescription())
                    .createdAt(clan.getCreatedAt())
                    .myRole(m.getRole().name())
                    .membershipStatus(m.getStatus().name())
                    .memberCount(members.size())
                    .members(members)
                    .pendingApplications(pending)
                    .score(finalScore)
                    .tier(currentClanTier)
                    .build();
        }

        Optional<ClanMembership> pending = membershipRepository
                .findByUserIdAndStatus(userId, MembershipStatus.PENDING);
        if (pending.isPresent()) {
            ClanMembership mp = pending.get();
            return ClanResponse.builder()
                    .id(mp.getClan().getId())
                    .name(mp.getClan().getName())
                    .membershipStatus(MembershipStatus.PENDING.name())
                    .build();
        }

        return null;
    }

    private MembershipResponse toMembershipResponse(ClanMembership m) {
        String displayName = userRepository.findByStringId(m.getUserId())
                .map(User::getDisplayName)
                .orElse("Unknown");

        return MembershipResponse.builder()
                .id(m.getId())
                .clanId(m.getClan().getId())
                .userId(m.getUserId())
                .displayName(displayName)
                .role(m.getRole())
                .status(m.getStatus())
                .build();
    }

    @Override
    @Transactional
    public void kickMember(String clanId, String targetUserId, String leaderId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan dengan id " + clanId + " tidak ditemukan"));

        membershipRepository.findByClanIdAndUserId(clanId, leaderId)
                .filter(m -> m.getRole() == ClanRole.LEADER)
                .orElseThrow(() -> new RuntimeException("Hanya ketua clan yang dapat menghapus clan"));

        if (leaderId.equals(targetUserId)) {
            throw new RuntimeException("Ketua tidak bisa mengeluarkan diri sendiri melalui fitur kick.");
        }

        ClanMembership targetMembership = membershipRepository.findByClanIdAndUserId(clanId, targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user bukan anggota clan ini."));

        membershipRepository.delete(targetMembership);
    }
}
