package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.micrometer.core.instrument.Counter;
import static org.mockito.Mockito.lenient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceImplTest {

    @Mock
    private ClanRepository clanRepository;

    @Mock
    private LeaderboardRankingBuilder rankingBuilder;

    @Mock private ClanMetricsService metricsService;

    @InjectMocks
    private LeaderboardServiceImpl leaderboardService;

    private Clan clan1;
    private Clan clan2;

    private LeaderboardEntryResponse entry1;
    private LeaderboardEntryResponse entry2;

    @BeforeEach
    void setUp() {
        clan1 = Clan.builder().id("clan1").name("Clan Satu").tier("BRONZE").build();
        clan2 = Clan.builder().id("clan2").name("Clan Dua").tier("BRONZE").build();
        entry1 = LeaderboardEntryResponse.builder()
                .clanId("clan1").clanName("Clan Satu").tier("BRONZE").score(150.0).rank(2).build();
        entry2 = LeaderboardEntryResponse.builder()
                .clanId("clan2").clanName("Clan Dua").tier("BRONZE").score(200.0).rank(1).build();
        Counter mockCounter = mock(Counter.class);
        lenient().when(metricsService.getLeaderboardViewedCounter()).thenReturn(mockCounter);
        lenient().when(metricsService.getLeaderboardByTierViewedCounter()).thenReturn(mockCounter);
    }

    @Test
    void getLeaderboardByTier_shouldReturnCorrectTier() {
        when(clanRepository.findByTier("BRONZE")).thenReturn(List.of(clan1, clan2));
        when(rankingBuilder.build(List.of(clan1, clan2))).thenReturn(List.of(entry2, entry1));

        TierLeaderboardResponse res = leaderboardService.getLeaderboardByTier("BRONZE");

        assertEquals("BRONZE", res.getTier());
    }

    @Test
    void getLeaderboardByTier_shouldReturnRankingsInCorrectOrder() {
        when(clanRepository.findByTier("BRONZE")).thenReturn(List.of(clan1, clan2));
        when(rankingBuilder.build(List.of(clan1, clan2))).thenReturn(List.of(entry2, entry1));

        TierLeaderboardResponse res = leaderboardService.getLeaderboardByTier("BRONZE");

        assertEquals("clan2", res.getRankings().get(0).getClanId());
        assertEquals(1, res.getRankings().get(0).getRank());
        assertEquals(200.0, res.getRankings().get(0).getScore());
    }

    @Test
    void getLeaderboardByTier_shouldReturnSecondRankCorrectly() {
        when(clanRepository.findByTier("BRONZE")).thenReturn(List.of(clan1, clan2));
        when(rankingBuilder.build(List.of(clan1, clan2))).thenReturn(List.of(entry2, entry1));

        TierLeaderboardResponse res = leaderboardService.getLeaderboardByTier("BRONZE");

        assertEquals("clan1", res.getRankings().get(1).getClanId());
        assertEquals(2, res.getRankings().get(1).getRank());
        assertEquals(150.0, res.getRankings().get(1).getScore());
    }

    @Test
    void getLeaderboardByTier_shouldCallRepositoryWithCorrectTier() {
        when(clanRepository.findByTier("BRONZE")).thenReturn(List.of(clan1));
        when(rankingBuilder.build(any())).thenReturn(List.of(entry2));

        leaderboardService.getLeaderboardByTier("BRONZE");

        verify(clanRepository, times(1)).findByTier("BRONZE");
    }

    @Test
    void getLeaderboardByTier_shouldCallRankingBuilderWithClansFromRepo() {
        when(clanRepository.findByTier("BRONZE")).thenReturn(List.of(clan1, clan2));
        when(rankingBuilder.build(List.of(clan1, clan2))).thenReturn(List.of(entry2, entry1));

        leaderboardService.getLeaderboardByTier("BRONZE");

        verify(rankingBuilder, times(1)).build(List.of(clan1, clan2));
    }

    @Test
    void getLeaderboardByTier_shouldReturnEmptyRankings_whenNoClansInTier() {
        when(clanRepository.findByTier("DIAMOND")).thenReturn(List.of());
        when(rankingBuilder.build(List.of())).thenReturn(List.of());

        TierLeaderboardResponse res = leaderboardService.getLeaderboardByTier("DIAMOND");

        assertNotNull(res);
        assertEquals("DIAMOND", res.getTier());
        assertTrue(res.getRankings().isEmpty());
    }

    @Test
    void getLeaderboardByTier_shouldThrowException_whenTierInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> leaderboardService.getLeaderboardByTier("INVALID_TIER"));

        verifyNoInteractions(clanRepository);
        verifyNoInteractions(rankingBuilder);
    }

    @Test
    void getAllTierLeaderboards_shouldReturnOneResponsePerTier() {
        when(clanRepository.findByTier(any())).thenReturn(List.of());
        when(rankingBuilder.build(any())).thenReturn(List.of());

        List<TierLeaderboardResponse> all = leaderboardService.getAllTierLeaderboards();

        assertFalse(all.isEmpty());
        assertEquals(id.ac.ui.cs.advprog.gatra.clan.model.ClanTier.values().length, all.size());
    }

    @Test
    void getAllTierLeaderboards_shouldCallRepoForEachTier() {
        when(clanRepository.findByTier(any())).thenReturn(List.of());
        when(rankingBuilder.build(any())).thenReturn(List.of());

        leaderboardService.getAllTierLeaderboards();

        verify(clanRepository, times(id.ac.ui.cs.advprog.gatra.clan.model.ClanTier.values().length))
                .findByTier(any());
    }
}