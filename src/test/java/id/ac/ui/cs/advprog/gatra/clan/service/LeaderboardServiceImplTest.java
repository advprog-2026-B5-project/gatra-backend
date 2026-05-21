package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceImplTest {

    @Mock private ClanRepository clanRepository;
    @Mock private BuffDebuffService buffDebuffService;

    @InjectMocks private LeaderboardServiceImpl leaderboardService;

    private Clan clan1;
    private Clan clan2;

    @BeforeEach
    void setUp() {
        clan1 = Clan.builder().id("clan1").name("Clan Satu").tier("BRONZE").build();
        clan2 = Clan.builder().id("clan2").name("Clan Dua").tier("BRONZE").build();
    }

    @Test
    void getLeaderboardByTier_success() {
        when(clanRepository.findByTier("BRONZE")).thenReturn(List.of(clan1, clan2));

        when(buffDebuffService.buildCalculator("clan1")).thenReturn((id, tier) -> 150.0);
        when(buffDebuffService.buildCalculator("clan2")).thenReturn((id, tier) -> 200.0);

        TierLeaderboardResponse res = leaderboardService.getLeaderboardByTier("BRONZE");

        // clan2 yang skornya lebih tinggi harus di rank 1
        assertEquals("clan2", res.getRankings().get(0).getClanId());
        assertEquals(200.0, res.getRankings().get(0).getScore());
        assertEquals(1, res.getRankings().get(0).getRank());

        assertEquals("clan1", res.getRankings().get(1).getClanId());
        assertEquals(150.0, res.getRankings().get(1).getScore());
        assertEquals(2, res.getRankings().get(1).getRank());


    }

    @Test
    void getAllTierLeaderboards_success() {
        when(clanRepository.findByTier(any())).thenReturn(List.of());
        
        List<TierLeaderboardResponse> all = leaderboardService.getAllTierLeaderboards();
        
        assertFalse(all.isEmpty());
        verify(clanRepository, atLeast(3)).findByTier(any()); 
    }

    @Test
    void getLeaderboardByTier_emptyTier_returnsEmptyRankings() {
        when(clanRepository.findByTier("DIAMOND")).thenReturn(List.of());

        TierLeaderboardResponse res = leaderboardService.getLeaderboardByTier("DIAMOND");

        assertNotNull(res);
        assertEquals("DIAMOND", res.getTier());
        assertTrue(res.getRankings().isEmpty());
    }
}
