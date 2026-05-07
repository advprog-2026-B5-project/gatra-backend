package id.ac.ui.cs.advprog.gatra.scoring.service;

import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import id.ac.ui.cs.advprog.gatra.scoring.strategy.TierScoringStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClanScoringServiceImpl implements ClanScoringService {

    private final Map<String, TierScoringStrategy> scoringStrategies;
    private final ClanMembershipRepository clanMembershipRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Autowired
    public ClanScoringServiceImpl(List<TierScoringStrategy> strategyList,
                                  ClanMembershipRepository clanMembershipRepository,
                                  PointHistoryRepository pointHistoryRepository) {

        // Dynamically wire all strategies into a Map keyed by Tier Name
        this.scoringStrategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getTierName().toUpperCase(),
                        strategy -> strategy
                ));
        this.clanMembershipRepository = clanMembershipRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    @Override
    public double calculateClanScore(String clanId, String tier, List<ScoreModifier> activeModifiers) {
        // Fetch only APPROVED members dynamically
        long totalMembers = clanMembershipRepository.countByClanIdAndStatus(clanId, MembershipStatus.APPROVED);
        double totalPoints = pointHistoryRepository.sumPointsByClanId(clanId);

        TierScoringStrategy strategy = scoringStrategies.get(tier.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported clan tier: " + tier);
        }

        double finalScore = strategy.calculateBaseScore(totalPoints, totalMembers);

        if (activeModifiers != null && !activeModifiers.isEmpty()) {
            for (ScoreModifier modifier : activeModifiers) {
                finalScore = modifier.apply(finalScore);
            }
        }

        return finalScore;
    }
}