package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.SeasonSnapshot;
import id.ac.ui.cs.advprog.gatra.clan.repository.SeasonSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeasonSnapshotManager {

    private final SeasonSnapshotRepository snapshotRepository;

    public int resolveNextSeasonNumber() {
        return snapshotRepository.findAll().stream()
                .mapToInt(SeasonSnapshot::getSeasonNumber)
                .max()
                .orElse(0) + 1;
    }

    public int resolveLastSeasonNumber() {
        return snapshotRepository.findAll().stream()
                .mapToInt(SeasonSnapshot::getSeasonNumber)
                .max()
                .orElseThrow(() -> new RuntimeException("Belum ada musim yang selesai."));
    }

    public void saveSnapshots(List<TierLeaderboardResponse> leaderboards,
                              int seasonNumber, LocalDateTime now) {
        leaderboards.forEach(tierBoard ->
                tierBoard.getRankings().forEach(entry ->
                        snapshotRepository.save(SeasonSnapshot.builder()
                                .clanId(entry.getClanId())
                                .clanName(entry.getClanName())
                                .tier(entry.getTier())
                                .finalScore(entry.getScore())
                                .finalRank(entry.getRank())
                                .seasonNumber(seasonNumber)
                                .snapshotAt(now)
                                .build())
                )
        );
    }

    public List<SeasonSnapshot> findBySeasonNumber(int seasonNumber) {
        return snapshotRepository
                .findBySeasonNumberOrderByTierAscFinalRankAsc(seasonNumber);
    }
}