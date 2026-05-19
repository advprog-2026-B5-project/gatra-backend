package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.model.SeasonSnapshot;
import id.ac.ui.cs.advprog.gatra.clan.repository.SeasonSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanSeasonServiceImplTest {

    @Mock private LeaderboardService leaderboardService;
    @Mock private SeasonSnapshotRepository snapshotRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private TierMigrationService tierMigrationService;

    @InjectMocks private ClanSeasonServiceImpl clanSeasonService;

    @Test
    void endSeason_success() {
        when(snapshotRepository.findAll()).thenReturn(List.of());
        
        LeaderboardEntryResponse entry = LeaderboardEntryResponse.builder().clanId("c1").clanName("Clan 1").tier("BRONZE").score(100.0).rank(1).build();
        TierLeaderboardResponse board = TierLeaderboardResponse.builder().tier("BRONZE").rankings(List.of(entry)).build();
        
        when(leaderboardService.getAllTierLeaderboards()).thenReturn(List.of(board));
        when(snapshotRepository.save(any(SeasonSnapshot.class))).thenReturn(new SeasonSnapshot());
        doNothing().when(tierMigrationService).migrate(any());

        SeasonResultResponse res = clanSeasonService.endSeason();

        assertNotNull(res);
        assertEquals(1, res.getSeasonNumber());
        assertEquals(1, res.getLeaderboards().size());
        
        verify(snapshotRepository, times(1)).save(any(SeasonSnapshot.class));
        verify(tierMigrationService, times(1)).migrate(any());
    }

    @Test
    void getLastSeasonResult_success() {
        SeasonSnapshot snap = SeasonSnapshot.builder().seasonNumber(2).tier("SILVER").clanId("c1").finalRank(1).finalScore(200.0).snapshotAt(LocalDateTime.now()).build();
        
        when(snapshotRepository.findAll()).thenReturn(List.of(snap));
        when(snapshotRepository.findBySeasonNumberOrderByTierAscFinalRankAsc(2)).thenReturn(List.of(snap));

        SeasonResultResponse res = clanSeasonService.getLastSeasonResult();

        assertNotNull(res);
        assertEquals(2, res.getSeasonNumber());
        assertEquals(1, res.getLeaderboards().size());
        assertEquals("SILVER", res.getLeaderboards().get(0).getTier());
    }

    @Test
    void getLastSeasonResult_throwsWhenNoSeasons() {
        when(snapshotRepository.findAll()).thenReturn(List.of());
        assertThrows(RuntimeException.class, () -> clanSeasonService.getLastSeasonResult());
    }
}
