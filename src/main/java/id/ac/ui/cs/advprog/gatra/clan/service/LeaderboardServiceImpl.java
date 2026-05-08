package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanTier;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import id.ac.ui.cs.advprog.gatra.scoring.service.ClanScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final ClanRepository clanRepository;
    private final ClanScoringService clanScoringService;
    private final BuffDebuffService buffDebuffService;

    @Override
    public TierLeaderboardResponse getLeaderboardByTier(String tier) {
        List<Clan> clansInTier = clanRepository.findByTier(tier);
        List<LeaderboardEntryResponse> rankings = buildRankings(clansInTier, tier);
        return TierLeaderboardResponse.builder()
                .tier(tier)
                .rankings(rankings)
                .build();
    }

    private List<LeaderboardEntryResponse> buildRankings(List<Clan> clans, String tier) {
        List<LeaderboardEntryResponse> entries = clans.stream()
                .map(clan -> toLeaderboardEntry(clan, tier))
                .sorted(Comparator.comparingDouble(LeaderboardEntryResponse::getScore).reversed())
                .toList();

        return assignRanks(entries);
    }

    private LeaderboardEntryResponse toLeaderboardEntry(Clan clan, String tier) {
        ScoreModifier modifier = buffDebuffService.getModifier(clan.getId());
        double finalScore = clanScoringService.calculateClanScore(
                clan.getId(),
                tier,
                List.of(modifier)
        );

        return LeaderboardEntryResponse.builder()
                .clanId(clan.getId())
                .clanName(clan.getName())
                .tier(tier)
                .score(finalScore)
                .build();
    }

    private List<LeaderboardEntryResponse> assignRanks(List<LeaderboardEntryResponse> entries) {
        AtomicInteger rank = new AtomicInteger(1);
        return entries.stream()
                .map(entry -> LeaderboardEntryResponse.builder()
                        .rank(rank.getAndIncrement())
                        .clanId(entry.getClanId())
                        .clanName(entry.getClanName())
                        .tier(entry.getTier())
                        .score(entry.getScore())
                        .build())
                .toList();
    }

    @Override
    public List<TierLeaderboardResponse> getAllTierLeaderboards() {
        return Arrays.stream(ClanTier.values())
                .map(tier -> getLeaderboardByTier(tier.name()))
                .toList();
    }
}