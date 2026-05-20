package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import id.ac.ui.cs.advprog.gatra.scoring.service.ClanScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceImplTest {

    @Mock private ClanRepository clanRepository;
    @Mock private ClanScoringService clanScoringService;
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
        when(buffDebuffService.getModifier(any())).thenReturn(new ScoreModifier("DUMMY", 1.0));
        when(clanScoringService.calculateClanScore(eq("clan1"), eq("BRONZE"), any())).thenReturn(150.0);
        when(clanScoringService.calculateClanScore(eq("clan2"), eq("BRONZE"), any())).thenReturn(200.0);

        TierLeaderboardResponse res = leaderboardService.getLeaderboardByTier("BRONZE");

        assertNotNull(res);
        assertEquals("BRONZE", res.getTier());
        assertEquals(2, res.getRankings().size());

        LeaderboardEntryResponse rank1 = res.getRankings().get(0);
        assertEquals("clan2", rank1.getClanId());
        assertEquals(1, rank1.getRank());
        assertEquals(200.0, rank1.getScore());

        LeaderboardEntryResponse rank2 = res.getRankings().get(1);
        assertEquals("clan1", rank2.getClanId());
        assertEquals(2, rank2.getRank());
        assertEquals(150.0, rank2.getScore());
    }

    @Test
    void getAllTierLeaderboards_success() {
        when(clanRepository.findByTier(any())).thenReturn(List.of());
        
        List<TierLeaderboardResponse> all = leaderboardService.getAllTierLeaderboards();
        
        assertFalse(all.isEmpty());
        verify(clanRepository, atLeast(3)).findByTier(any()); 
    }
}
