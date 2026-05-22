package id.ac.ui.cs.advprog.gatra.clan.service;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TierMigrationServiceImpl implements TierMigrationService {

    private final TierMigrationProcessor migrationProcessor;

    @Override
    @Transactional
    public void migrate(List<TierLeaderboardResponse> leaderboards) {
        leaderboards.forEach(migrationProcessor::processTierMigration);
    }
}