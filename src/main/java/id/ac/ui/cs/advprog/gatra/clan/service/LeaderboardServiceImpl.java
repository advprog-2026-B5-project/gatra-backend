package id.ac.ui.cs.advprog.gatra.clan.service;
import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanTier;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final ClanRepository clanRepository;
    private final LeaderboardRankingBuilder rankingBuilder;
    private final ClanMetricsService metricsService;

    @Override
    public TierLeaderboardResponse getLeaderboardByTier(String tier) {
        ClanTier.valueOf(tier.toUpperCase());
        List<Clan> clansInTier = clanRepository.findByTier(tier);
        List<LeaderboardEntryResponse> rankings = rankingBuilder.build(clansInTier);
        metricsService.getLeaderboardByTierViewedCounter().increment();
        return TierLeaderboardResponse.builder()
                .tier(tier)
                .rankings(rankings)
                .build();
    }

    @Override
    public List<TierLeaderboardResponse> getAllTierLeaderboards() {
        metricsService.getLeaderboardViewedCounter().increment();
        return Arrays.stream(ClanTier.values())
                .map(tier -> getLeaderboardByTier(tier.name()))
                .toList();

    }
}