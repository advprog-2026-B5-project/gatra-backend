package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.MembershipResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanRole;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClanResponseMapper {

    private final ClanMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final BuffDebuffService buffDebuffService;

    public ClanResponse toSimpleResponse(Clan clan) {
        List<MembershipResponse> members = fetchApprovedMembers(clan.getId());
        return ClanResponse.builder()
                .id(clan.getId())
                .name(clan.getName())
                .description(clan.getDescription())
                .createdAt(clan.getCreatedAt())
                .memberCount(members.size())
                .score(calculateScore(clan))
                .tier(clan.getTier())
                .build();
    }

    public ClanResponse toApprovedMemberResponse(ClanMembership m) {
        Clan clan = m.getClan();
        List<MembershipResponse> members = fetchApprovedMembers(clan.getId());
        return ClanResponse.builder()
                .id(clan.getId())
                .name(clan.getName())
                .description(clan.getDescription())
                .createdAt(clan.getCreatedAt())
                .myRole(m.getRole().name())
                .membershipStatus(m.getStatus().name())
                .memberCount(members.size())
                .members(members)
                .pendingApplications(fetchPendingIfLeader(m))
                .score(calculateScore(clan))
                .tier(clan.getTier())
                .build();
    }

    public ClanResponse toPendingResponse(ClanMembership mp) {
        return ClanResponse.builder()
                .id(mp.getClan().getId())
                .name(mp.getClan().getName())
                .membershipStatus(MembershipStatus.PENDING.name())
                .build();
    }

    public MembershipResponse toMembershipResponse(ClanMembership m) {
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

    private double calculateScore(Clan clan) {
        return buffDebuffService.buildCalculator(clan.getId())
                .calculate(clan.getId(), clan.getTier());
    }

    private List<MembershipResponse> fetchApprovedMembers(String clanId) {
        return membershipRepository
                .findByClanIdAndStatus(clanId, MembershipStatus.APPROVED)
                .stream().map(this::toMembershipResponse).toList();
    }

    private List<MembershipResponse> fetchPendingIfLeader(ClanMembership m) {
        if (m.getRole() != ClanRole.LEADER) return List.of();
        return membershipRepository
                .findByClanIdAndStatus(m.getClan().getId(), MembershipStatus.PENDING)
                .stream().map(this::toMembershipResponse).toList();
    }
}