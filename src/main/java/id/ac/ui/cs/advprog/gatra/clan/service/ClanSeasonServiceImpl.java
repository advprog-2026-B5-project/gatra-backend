package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.event.SeasonEndedEvent;
import id.ac.ui.cs.advprog.gatra.clan.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClanSeasonServiceImpl implements ClanSeasonService {

    private final LeaderboardService leaderboardService;
    private final SeasonSnapshotManager snapshotManager;
    private final SeasonSnapshotMapper snapshotMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TierMigrationService tierMigrationService;
    private final ClanMetricsService metricsService;

    @Override
    @Transactional
    public SeasonResultResponse endSeason() {
        int seasonNumber = snapshotManager.resolveNextSeasonNumber();
        List<TierLeaderboardResponse> leaderboards = leaderboardService.getAllTierLeaderboards();
        LocalDateTime now = LocalDateTime.now();

        snapshotManager.saveSnapshots(leaderboards, seasonNumber, now);
        tierMigrationService.migrate(leaderboards);
        eventPublisher.publishEvent(new SeasonEndedEvent(leaderboards));

        metricsService.getSeasonResetCounter().increment();
        return SeasonResultResponse.builder()
                .seasonNumber(seasonNumber)
                .frozenAt(now)
                .leaderboards(leaderboards)
                .build();
    }

    @Override
    public SeasonResultResponse getLastSeasonResult() {
        int lastSeason = snapshotManager.resolveLastSeasonNumber();
        List<SeasonSnapshot> snapshots = snapshotManager.findBySeasonNumber(lastSeason);
        LocalDateTime frozenAt = snapshots.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Snapshot tidak ditemukan untuk season: " + lastSeason))
                .getSnapshotAt();

        return SeasonResultResponse.builder()
                .seasonNumber(lastSeason)
                .frozenAt(frozenAt)
                .leaderboards(snapshotMapper.groupSnapshotsByTier(snapshots))
                .build();
    }
}

