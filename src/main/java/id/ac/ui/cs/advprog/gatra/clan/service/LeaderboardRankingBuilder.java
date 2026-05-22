package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class LeaderboardRankingBuilder {

    private final LeaderboardEntryMapper entryMapper;

    public List<LeaderboardEntryResponse> build(List<Clan> clans) {
        List<LeaderboardEntryResponse> entries = clans.stream()
                .map(entryMapper::toEntry)
                .sorted(Comparator.comparingDouble(LeaderboardEntryResponse::getScore).reversed())
                .toList();
        return assignRanks(entries);
    }

    private List<LeaderboardEntryResponse> assignRanks(List<LeaderboardEntryResponse> entries) {
        return IntStream.range(0, entries.size())
                .mapToObj(i -> entries.get(i).toBuilder().rank(i + 1).build())
                .toList();
    }
}
