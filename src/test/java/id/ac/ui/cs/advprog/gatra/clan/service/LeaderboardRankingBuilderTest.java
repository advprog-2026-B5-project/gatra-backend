package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardRankingBuilderTest {

    @Mock
    private LeaderboardEntryMapper entryMapper;

    @InjectMocks
    private LeaderboardRankingBuilder leaderboardRankingBuilder;

    private Clan clanA;
    private Clan clanB;
    private Clan clanC;

    private LeaderboardEntryResponse entryA;
    private LeaderboardEntryResponse entryB;
    private LeaderboardEntryResponse entryC;

    @BeforeEach
    void setUp() {
        clanA = Clan.builder().id("clan-a").name("Alpha").tier("GOLD").build();
        clanB = Clan.builder().id("clan-b").name("Beta").tier("SILVER").build();
        clanC = Clan.builder().id("clan-c").name("Gamma").tier("BRONZE").build();

        entryA = LeaderboardEntryResponse.builder().clanId("clan-a").clanName("Alpha").tier("GOLD").score(900.0).build();
        entryB = LeaderboardEntryResponse.builder().clanId("clan-b").clanName("Beta").tier("SILVER").score(600.0).build();
        entryC = LeaderboardEntryResponse.builder().clanId("clan-c").clanName("Gamma").tier("BRONZE").score(300.0).build();
    }

    @Test
    void build_shouldReturnSameSizeAsInputClans() {
        when(entryMapper.toEntry(clanA)).thenReturn(entryA);
        when(entryMapper.toEntry(clanB)).thenReturn(entryB);
        when(entryMapper.toEntry(clanC)).thenReturn(entryC);

        List<LeaderboardEntryResponse> result = leaderboardRankingBuilder.build(List.of(clanA, clanB, clanC));

        assertEquals(3, result.size());
    }

    @Test
    void build_shouldSortEntriesByScoreDescending() {
        when(entryMapper.toEntry(clanA)).thenReturn(entryA);
        when(entryMapper.toEntry(clanB)).thenReturn(entryB);
        when(entryMapper.toEntry(clanC)).thenReturn(entryC);

        // input sengaja acak
        List<LeaderboardEntryResponse> result = leaderboardRankingBuilder.build(List.of(clanC, clanA, clanB));

        assertEquals("clan-a", result.get(0).getClanId());
        assertEquals("clan-b", result.get(1).getClanId());
        assertEquals("clan-c", result.get(2).getClanId());
    }

    @Test
    void build_shouldAssignRankStartingFromOne() {
        when(entryMapper.toEntry(clanA)).thenReturn(entryA);
        when(entryMapper.toEntry(clanB)).thenReturn(entryB);
        when(entryMapper.toEntry(clanC)).thenReturn(entryC);

        List<LeaderboardEntryResponse> result = leaderboardRankingBuilder.build(List.of(clanA, clanB, clanC));

        assertEquals(1, result.get(0).getRank());
    }

    @Test
    void build_shouldAssignRanksSequentially() {
        when(entryMapper.toEntry(clanA)).thenReturn(entryA);
        when(entryMapper.toEntry(clanB)).thenReturn(entryB);
        when(entryMapper.toEntry(clanC)).thenReturn(entryC);

        List<LeaderboardEntryResponse> result = leaderboardRankingBuilder.build(List.of(clanA, clanB, clanC));

        assertEquals(1, result.get(0).getRank());
        assertEquals(2, result.get(1).getRank());
        assertEquals(3, result.get(2).getRank());
    }

    @Test
    void build_shouldCallToEntryForEachClan() {
        when(entryMapper.toEntry(clanA)).thenReturn(entryA);
        when(entryMapper.toEntry(clanB)).thenReturn(entryB);
        when(entryMapper.toEntry(clanC)).thenReturn(entryC);

        leaderboardRankingBuilder.build(List.of(clanA, clanB, clanC));

        verify(entryMapper, times(1)).toEntry(clanA);
        verify(entryMapper, times(1)).toEntry(clanB);
        verify(entryMapper, times(1)).toEntry(clanC);
    }

    @Test
    void build_shouldReturnEmptyList_whenInputIsEmpty() {
        List<LeaderboardEntryResponse> result = leaderboardRankingBuilder.build(List.of());

        assertTrue(result.isEmpty());
        verifyNoInteractions(entryMapper);
    }

    @Test
    void build_shouldReturnRankOne_whenOnlyOneClan() {
        when(entryMapper.toEntry(clanA)).thenReturn(entryA);

        List<LeaderboardEntryResponse> result = leaderboardRankingBuilder.build(List.of(clanA));

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals("clan-a", result.get(0).getClanId());
    }

    @Test
    void build_shouldPreserveHighestScoreAtRankOne() {
        when(entryMapper.toEntry(clanA)).thenReturn(entryA);
        when(entryMapper.toEntry(clanB)).thenReturn(entryB);
        when(entryMapper.toEntry(clanC)).thenReturn(entryC);

        List<LeaderboardEntryResponse> result = leaderboardRankingBuilder.build(List.of(clanA, clanB, clanC));

        assertEquals(900.0, result.get(0).getScore());
    }

    @Test
    void build_shouldPreserveLowestScoreAtLastRank() {
        when(entryMapper.toEntry(clanA)).thenReturn(entryA);
        when(entryMapper.toEntry(clanB)).thenReturn(entryB);
        when(entryMapper.toEntry(clanC)).thenReturn(entryC);

        List<LeaderboardEntryResponse> result = leaderboardRankingBuilder.build(List.of(clanA, clanB, clanC));

        assertEquals(300.0, result.get(2).getScore());
        assertEquals(3, result.get(2).getRank());
    }

    @Test
    void build_shouldNotMutateOriginalEntryScore() {
        when(entryMapper.toEntry(clanA)).thenReturn(entryA);

        leaderboardRankingBuilder.build(List.of(clanA));

        assertEquals(900.0, entryA.getScore());
        assertEquals(0, entryA.getRank());
    }
}