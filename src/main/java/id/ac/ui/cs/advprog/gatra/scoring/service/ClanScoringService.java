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
public class ClanScoringService {

    private final Map<String, TierScoringStrategy> scoringStrategies;

    // Using ClanMembershipRepository to strictly handle membership aggregates
    private final ClanMembershipRepository clanMembershipRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Autowired
    public ClanScoringService(List<TierScoringStrategy> strategyList,
                              ClanMembershipRepository clanMembershipRepository,
                              PointHistoryRepository pointHistoryRepository) {

        this.scoringStrategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getTierName().toUpperCase(),
                        strategy -> strategy
                ));
        this.clanMembershipRepository = clanMembershipRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public double calculateClanScore(String clanId, String tier, List<ScoreModifier> activeModifiers) {

        // Fetch only APPROVED members dynamically via the membership repository
        int totalMembers = clanMembershipRepository.countByClan_IdAndStatus(clanId, MembershipStatus.APPROVED);

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