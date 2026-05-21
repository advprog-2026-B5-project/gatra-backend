package id.ac.ui.cs.advprog.gatra.clan.decorator;

import id.ac.ui.cs.advprog.gatra.scoring.service.ClanScoringService;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class BaseScoreCalculator implements ScoreCalculator {
    private final ClanScoringService scoringService;

    @Override
    public double calculate(String clanId, String tier) {
        return scoringService.calculateClanScore(clanId, tier, List.of());
    }
}