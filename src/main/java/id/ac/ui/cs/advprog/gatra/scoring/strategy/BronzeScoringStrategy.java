package id.ac.ui.cs.advprog.gatra.scoring.strategy;

import org.springframework.stereotype.Component;

@Component
public class BronzeScoringStrategy implements TierScoringStrategy {

    @Override
    public String getTierName() {
        return "BRONZE";
    }

    @Override
    public double calculateBaseScore(double totalPointsEarned, int totalMembers) {
        // Bronze Tier logic: Simple summation of all points
        return totalPointsEarned;
    }
}