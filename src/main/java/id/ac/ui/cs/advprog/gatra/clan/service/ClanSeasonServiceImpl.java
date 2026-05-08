package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.model.*;
import id.ac.ui.cs.advprog.gatra.clan.repository.SeasonSnapshotRepository;
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
    private final SeasonSnapshotRepository snapshotRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public SeasonResultResponse endSeason() {
        int newSeasonNumber = snapshotRepository
                .findAll()
                .stream()
                .mapToInt(SeasonSnapshot::getSeasonNumber)
                .max()
                .orElse(0) + 1;

        // ambil final leaderboard
        List<TierLeaderboardResponse> leaderboards = leaderboardService.getAllTierLeaderboards();

        LocalDateTime now = LocalDateTime.now();
        for (TierLeaderboardResponse tierBoard : leaderboards) {
            for (LeaderboardEntryResponse entry : tierBoard.getRankings()) {
                SeasonSnapshot snapshot = SeasonSnapshot.builder()
                        .clanId(entry.getClanId())
                        .clanName(entry.getClanName())
                        .tier(entry.getTier())
                        .finalScore(entry.getScore())
                        .finalRank(entry.getRank())
                        .seasonNumber(newSeasonNumber)
                        .snapshotAt(now)
                        .build();
                snapshotRepository.save(snapshot);
            }

        }

        return SeasonResultResponse.builder()
                .seasonNumber(newSeasonNumber)
                .frozenAt(now)
                .leaderboards(leaderboards)
                .build();
    }

    @Override
    public SeasonResultResponse getLastSeasonResult() {
        int lastSeason = snapshotRepository
                .findAll()
                .stream()
                .mapToInt(SeasonSnapshot::getSeasonNumber)
                .max()
                .orElseThrow(() -> new RuntimeException("Belum ada musim yang selesai."));

        List<SeasonSnapshot> snapshots = snapshotRepository
                .findBySeasonNumberOrderByTierAscFinalRankAsc(lastSeason);

        // per tier
        List<TierLeaderboardResponse> leaderboards = snapshots.stream()
                .collect(java.util.stream.Collectors.groupingBy(SeasonSnapshot::getTier))
                .entrySet().stream()
                .map(entry -> TierLeaderboardResponse.builder()
                        .tier(entry.getKey())
                        .rankings(entry.getValue().stream()
                                .map(s -> LeaderboardEntryResponse.builder()
                                        .rank(s.getFinalRank())
                                        .clanId(s.getClanId())
                                        .clanName(s.getClanName())
                                        .tier(s.getTier())
                                        .score(s.getFinalScore())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return SeasonResultResponse.builder()
                .seasonNumber(lastSeason)
                .frozenAt(snapshots.get(0).getSnapshotAt())
                .leaderboards(leaderboards)
                .build();
    }
}