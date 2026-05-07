package id.ac.ui.cs.advprog.gatra.clan.strategy;

import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;

public interface BuffDebuffStrategy {
    boolean isApplicable(double completionRate);
    ScoreModifier getModifier();
}