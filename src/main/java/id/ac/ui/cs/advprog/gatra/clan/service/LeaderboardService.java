package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;

public interface LeaderboardService {
    TierLeaderboardResponse getLeaderboardByTier(String tier);
}