package id.ac.ui.cs.advprog.gatra.scoring.strategy;

import org.springframework.stereotype.Component;

@Component
public class DiamondScoringStrategy implements TierScoringStrategy {

    @Override
    public String getTierName() {
        return "DIAMOND";
    }

    @Override
    public double calculateBaseScore(double totalPointsEarned, int totalMembers) {
        // Prevent division by zero
        if (totalMembers <= 0) {
            return 0.0;
        }
        // Diamond Tier logic: Weighted Average
        return totalPointsEarned / totalMembers;
    }
}