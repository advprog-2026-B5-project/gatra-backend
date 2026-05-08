package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import java.util.List;

public interface TierMigrationService {
    void migrate(List<TierLeaderboardResponse> leaderboards);
}