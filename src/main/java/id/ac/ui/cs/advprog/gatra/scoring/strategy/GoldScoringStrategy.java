package id.ac.ui.cs.advprog.gatra.scoring.strategy;

import org.springframework.stereotype.Component;

@Component
public class GoldScoringStrategy implements TierScoringStrategy {

    // The baseline expectation for every member in a Gold clan
    private static final double QUOTA_PER_MEMBER = 200.0;

    // The massive bonus awarded only to points that exceed the quota
    private static final double OVERDRIVE_MULTIPLIER = 1.5;

    @Override
    public String getTierName() {
        return "GOLD";
    }

    @Override
    public double calculateBaseScore(double totalPointsEarned, int totalMembers) {
        if (totalMembers <= 0) {
            return 0.0;
        }

        // Calculate the threshold the clan needs to break
        double clanBaseQuota = totalMembers * QUOTA_PER_MEMBER;

        if (totalPointsEarned <= clanBaseQuota) {
            // They haven't hit the quota yet; standard points apply
            return totalPointsEarned;
        } else {
            // They broke the quota!
            // Standard points up to the quota + Overdrive points for the rest
            double overdrivePoints = totalPointsEarned - clanBaseQuota;
            return clanBaseQuota + (overdrivePoints * OVERDRIVE_MULTIPLIER);
        }
    }
}