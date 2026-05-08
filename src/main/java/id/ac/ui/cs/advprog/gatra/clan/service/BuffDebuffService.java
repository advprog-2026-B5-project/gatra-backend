package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;

public interface BuffDebuffService {
    ScoreModifier getModifier(String clanId);
}