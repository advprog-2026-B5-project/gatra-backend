package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.decorator.ScoreCalculator;

public interface BuffDebuffService {
    ScoreCalculator buildCalculator(String clanId);
}