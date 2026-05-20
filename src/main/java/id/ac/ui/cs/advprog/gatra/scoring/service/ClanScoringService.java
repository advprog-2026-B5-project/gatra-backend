package id.ac.ui.cs.advprog.gatra.scoring.service;

import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;

import java.util.List;

public interface ClanScoringService {
    double calculateClanScore(String clanId, String tier, List<ScoreModifier> activeModifiers);
}