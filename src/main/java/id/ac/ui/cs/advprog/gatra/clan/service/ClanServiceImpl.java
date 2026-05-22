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
public class ClanServiceImpl implements ClanService {

    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;
    private final ClanResponseMapper responseMapper;
    private final ClanValidator validator;
    private final ClanMetricsService metricsService;

    @Override
    @Transactional
    public ClanResponse createClan(CreateClanRequest request, String userId) {
        validator.validateUserNotInAnyClan(userId);

        Clan clan = Clan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        clanRepository.save(clan);

        membershipRepository.save(ClanMembership.builder()
                .clan(clan).userId(userId)
                .role(ClanRole.LEADER).status(MembershipStatus.APPROVED)
                .build());

        metricsService.getClanCreatedCounter().increment();

        return responseMapper.toSimpleResponse(clan);

    }

    @Override
    public ClanResponse getClan(String clanId) {
        return responseMapper.toSimpleResponse(validator.findClanOrThrow(clanId));
    }

    @Override
    @Transactional
    public void deleteClan(String clanId, String userId) {
        Clan clan = validator.findClanOrThrow(clanId);
        validator.validateLeader(clanId, userId);
        clanRepository.delete(clan);
        metricsService.getClanDeletedCounter().increment();
    }

    @Override
    public List<ClanResponse> getAllClans() {
        return clanRepository.findAll().stream()
                .map(responseMapper::toSimpleResponse)
                .toList();
    }

    @Override
    public ClanResponse getMyClan(String userId) {
        return membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.APPROVED)
                .map(responseMapper::toApprovedMemberResponse)
                .orElseGet(() -> membershipRepository
                        .findByUserIdAndStatus(userId, MembershipStatus.PENDING)
                        .map(responseMapper::toPendingResponse)
                        .orElse(null));
    }

    @Override
    @Transactional
    public void kickMember(String clanId, String targetUserId, String leaderId) {
        validator.findClanOrThrow(clanId);
        validator.validateLeader(clanId, leaderId);
        validator.validateNotSelfKick(leaderId, targetUserId);

        membershipRepository.delete(
                membershipRepository.findByClanIdAndUserId(clanId, targetUserId)
                        .orElseThrow(() -> new RuntimeException("Target user bukan anggota clan ini.")));

        metricsService.getMembershipKickedCounter().increment();
    }
}