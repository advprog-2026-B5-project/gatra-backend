package id.ac.ui.cs.advprog.gatra.scoring.strategy;

public interface TierScoringStrategy {
    String getTierName();
    double calculateBaseScore(double totalPointsEarned, long totalMembers);
}