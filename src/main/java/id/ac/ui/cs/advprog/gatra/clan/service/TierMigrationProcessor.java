package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanTier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class TierMigrationProcessor {

    private final TierResolver tierResolver;
    private final ClanTierUpdater clanTierUpdater;

    public void processTierMigration(TierLeaderboardResponse tierBoard) {
        ClanTier currentTier = ClanTier.valueOf(tierBoard.getTier());
        List<LeaderboardEntryResponse> rankings = tierBoard.getRankings();
        int totalClans = rankings.size();

        IntStream.range(0, totalClans).forEach(i -> {
            int rank = i + 1;
            ClanTier newTier = tierResolver.resolveNewTier(currentTier, rank, totalClans);
            clanTierUpdater.updateClanTier(rankings.get(i).getClanId(), newTier);
        });
    }
}
