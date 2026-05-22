package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.SeasonSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeasonSnapshotMapperTest {

    private SeasonSnapshotMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SeasonSnapshotMapper();
    }

    @Test
    void groupSnapshotsByTier_shouldReturnEmptyList_whenNoSnapshots() {
        List<TierLeaderboardResponse> result = mapper.groupSnapshotsByTier(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void groupSnapshotsByTier_shouldGroupByTierCorrectly() {
        SeasonSnapshot goldSnap1 = SeasonSnapshot.builder()
                .clanId("clan-1").clanName("Alpha").tier("GOLD")
                .finalScore(900.0).finalRank(1).seasonNumber(1).build();
        SeasonSnapshot goldSnap2 = SeasonSnapshot.builder()
                .clanId("clan-2").clanName("Beta").tier("GOLD")
                .finalScore(800.0).finalRank(2).seasonNumber(1).build();
        SeasonSnapshot silverSnap = SeasonSnapshot.builder()
                .clanId("clan-3").clanName("Gamma").tier("SILVER")
                .finalScore(600.0).finalRank(1).seasonNumber(1).build();

        List<TierLeaderboardResponse> result = mapper.groupSnapshotsByTier(
                List.of(goldSnap1, goldSnap2, silverSnap));

        assertEquals(2, result.size());
    }

    @Test
    void groupSnapshotsByTier_shouldMapEntryFieldsCorrectly() {
        SeasonSnapshot snap = SeasonSnapshot.builder()
                .clanId("clan-1").clanName("Alpha").tier("GOLD")
                .finalScore(900.0).finalRank(1).seasonNumber(1).build();

        List<TierLeaderboardResponse> result = mapper.groupSnapshotsByTier(List.of(snap));

        TierLeaderboardResponse tierBoard = result.get(0);
        assertEquals("GOLD", tierBoard.getTier());

        LeaderboardEntryResponse entry = tierBoard.getRankings().get(0);
        assertEquals("clan-1", entry.getClanId());
        assertEquals("Alpha", entry.getClanName());
        assertEquals("GOLD", entry.getTier());
        assertEquals(900.0, entry.getScore());
        assertEquals(1, entry.getRank());
    }

    @Test
    void groupSnapshotsByTier_shouldPutCorrectNumberOfEntriesPerTier() {
        SeasonSnapshot goldSnap1 = SeasonSnapshot.builder()
                .clanId("clan-1").clanName("Alpha").tier("GOLD")
                .finalScore(900.0).finalRank(1).seasonNumber(1).build();
        SeasonSnapshot goldSnap2 = SeasonSnapshot.builder()
                .clanId("clan-2").clanName("Beta").tier("GOLD")
                .finalScore(800.0).finalRank(2).seasonNumber(1).build();

        List<TierLeaderboardResponse> result = mapper.groupSnapshotsByTier(
                List.of(goldSnap1, goldSnap2));

        TierLeaderboardResponse goldBoard = result.stream()
                .filter(t -> t.getTier().equals("GOLD"))
                .findFirst().orElseThrow();

        assertEquals(2, goldBoard.getRankings().size());
    }

    @Test
    void groupSnapshotsByTier_shouldHandleSingleSnapshot() {
        SeasonSnapshot snap = SeasonSnapshot.builder()
                .clanId("clan-1").clanName("Alpha").tier("BRONZE")
                .finalScore(100.0).finalRank(1).seasonNumber(1).build();

        List<TierLeaderboardResponse> result = mapper.groupSnapshotsByTier(List.of(snap));

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getRankings().size());
    }
}