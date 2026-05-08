package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.event.ClanReachedDiamondEvent;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanTier;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TierMigrationServiceImpl implements TierMigrationService {

    private static final int PROMOTION_COUNT = 3;
    private static final int RELEGATION_COUNT = 3;

    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void migrate(List<TierLeaderboardResponse> leaderboards) {
        for (TierLeaderboardResponse tierBoard : leaderboards) {
            processTierMigration(tierBoard);
        }
    }

    private void processTierMigration(TierLeaderboardResponse tierBoard) {
        List<LeaderboardEntryResponse> rankings = tierBoard.getRankings();
        ClanTier currentTier = ClanTier.valueOf(tierBoard.getTier());

        int totalClans = rankings.size();

        for (int i = 0; i < totalClans; i++) {
            LeaderboardEntryResponse entry = rankings.get(i);
            int rank = i + 1;

            ClanTier newTier = resolveNewTier(currentTier, rank, totalClans);
            updateClanTier(entry.getClanId(), newTier);
        }
    }

    private ClanTier resolveNewTier(ClanTier currentTier, int rank, int totalClans) {
        if (isEligibleForPromotion(rank)) {
            return promoteTier(currentTier);
        }
        if (isEligibleForRelegation(rank, totalClans)) {
            return relegateTier(currentTier);
        }
        return currentTier;
    }

    private boolean isEligibleForPromotion(int rank) {
        return rank <= PROMOTION_COUNT;
    }

    private boolean isEligibleForRelegation(int rank, int totalClans) {
        return !isEligibleForPromotion(rank) && rank > totalClans - RELEGATION_COUNT;
    }

    private ClanTier promoteTier(ClanTier currentTier) {
        ClanTier[] tiers = ClanTier.values();
        int nextIndex = currentTier.ordinal() + 1;
        return nextIndex < tiers.length ? tiers[nextIndex] : currentTier;
    }

    private ClanTier relegateTier(ClanTier currentTier) {
        int prevIndex = currentTier.ordinal() - 1;
        return prevIndex >= 0 ? ClanTier.values()[prevIndex] : currentTier;
    }

    private void updateClanTier(String clanId, ClanTier newTier) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan tidak ditemukan: " + clanId));

        ClanTier oldTier = ClanTier.valueOf(clan.getTier());
        clan.setTier(newTier.name());
        clanRepository.save(clan);

        if (newTier == ClanTier.DIAMOND && oldTier != ClanTier.DIAMOND) {
            List<String> memberIds = membershipRepository
                    .findByClanIdAndStatus(clanId, MembershipStatus.APPROVED)
                    .stream()
                    .map(ClanMembership::getUserId)
                    .toList();

            eventPublisher.publishEvent(
                    new ClanReachedDiamondEvent(this, clanId, memberIds));
        }

    }
}