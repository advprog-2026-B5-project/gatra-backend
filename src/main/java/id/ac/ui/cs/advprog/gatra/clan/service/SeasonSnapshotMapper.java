package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.SeasonSnapshot;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeasonSnapshotMapper {

    public List<TierLeaderboardResponse> groupSnapshotsByTier(List<SeasonSnapshot> snapshots) {
        return snapshots.stream()
                .collect(Collectors.groupingBy(SeasonSnapshot::getTier))
                .entrySet().stream()
                .map(entry -> TierLeaderboardResponse.builder()
                        .tier(entry.getKey())
                        .rankings(entry.getValue().stream()
                                .map(this::toLeaderboardEntry)
                                .toList())
                        .build())
                .toList();
    }

    private LeaderboardEntryResponse toLeaderboardEntry(SeasonSnapshot s) {
        return LeaderboardEntryResponse.builder()
                .rank(s.getFinalRank())
                .clanId(s.getClanId())
                .clanName(s.getClanName())
                .tier(s.getTier())
                .score(s.getFinalScore())
                .build();
    }
}