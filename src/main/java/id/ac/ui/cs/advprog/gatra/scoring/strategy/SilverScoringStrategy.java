package id.ac.ui.cs.advprog.gatra.scoring.strategy;

import org.springframework.stereotype.Component;

@Component
public class SilverScoringStrategy implements TierScoringStrategy {

    private static final double SYNERGY_BONUS_PER_MEMBER = 50.0;

    @Override
    public String getTierName() {
        return "SILVER";
    }

    @Override
    public double calculateBaseScore(double totalPointsEarned, int totalMembers) {
        // Silver Tier logic: Base points + (50 points * number of active members)
        return totalPointsEarned + (totalMembers * SYNERGY_BONUS_PER_MEMBER);
    }
}