package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.event.SeasonEndedEvent;
import id.ac.ui.cs.advprog.gatra.clan.model.SeasonSnapshot;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ClanSeasonServiceImplTest {

    @Mock private LeaderboardService leaderboardService;
    @Mock private SeasonSnapshotManager snapshotManager;
    @Mock private SeasonSnapshotMapper snapshotMapper;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private TierMigrationService tierMigrationService;
    @Mock private ClanMetricsService metricsService;

    @InjectMocks private ClanSeasonServiceImpl clanSeasonService;

    private Counter mockCounter;

    @BeforeEach
    void setUp() {
        mockCounter = mock(Counter.class);
        lenient().when(metricsService.getSeasonResetCounter()).thenReturn(mockCounter);
    }

    @Test
    void endSeason_success() {
        LeaderboardEntryResponse entry = LeaderboardEntryResponse.builder()
                .clanId("c1").clanName("Clan 1").tier("BRONZE").score(100.0).rank(1).build();
        TierLeaderboardResponse board = TierLeaderboardResponse.builder()
                .tier("BRONZE").rankings(List.of(entry)).build();

        when(snapshotManager.resolveNextSeasonNumber()).thenReturn(2);
        when(leaderboardService.getAllTierLeaderboards()).thenReturn(List.of(board));
        doNothing().when(snapshotManager).saveSnapshots(any(), eq(2), any());
        doNothing().when(tierMigrationService).migrate(any());
        doNothing().when(eventPublisher).publishEvent(any(SeasonEndedEvent.class));

        SeasonResultResponse res = clanSeasonService.endSeason();

        assertNotNull(res);
        assertEquals(2, res.getSeasonNumber());
        assertNotNull(res.getFrozenAt());
        assertEquals(1, res.getLeaderboards().size());
        assertEquals("BRONZE", res.getLeaderboards().get(0).getTier());

        verify(snapshotManager).resolveNextSeasonNumber();
        verify(snapshotManager).saveSnapshots(eq(List.of(board)), eq(2), any());
        verify(tierMigrationService).migrate(List.of(board));
        verify(eventPublisher).publishEvent(any(SeasonEndedEvent.class));
    }

    @Test
    void endSeason_emptyLeaderboards_stillPublishesEvent() {
        when(snapshotManager.resolveNextSeasonNumber()).thenReturn(1);
        when(leaderboardService.getAllTierLeaderboards()).thenReturn(List.of());
        doNothing().when(snapshotManager).saveSnapshots(any(), eq(1), any());
        doNothing().when(tierMigrationService).migrate(any());

        SeasonResultResponse res = clanSeasonService.endSeason();

        assertNotNull(res);
        assertTrue(res.getLeaderboards().isEmpty());
        verify(eventPublisher).publishEvent(any(SeasonEndedEvent.class));
    }

    @Test
    void getLastSeasonResult_success() {
        LocalDateTime frozenAt = LocalDateTime.now();
        SeasonSnapshot snap = SeasonSnapshot.builder()
                .seasonNumber(3).tier("SILVER").clanId("c1")
                .finalRank(1).finalScore(200.0).snapshotAt(frozenAt).build();

        TierLeaderboardResponse board = TierLeaderboardResponse.builder()
                .tier("SILVER").rankings(List.of()).build();

        when(snapshotManager.resolveLastSeasonNumber()).thenReturn(3);
        when(snapshotManager.findBySeasonNumber(3)).thenReturn(List.of(snap));
        when(snapshotMapper.groupSnapshotsByTier(List.of(snap))).thenReturn(List.of(board));

        SeasonResultResponse res = clanSeasonService.getLastSeasonResult();

        assertNotNull(res);
        assertEquals(3, res.getSeasonNumber());
        assertEquals(frozenAt, res.getFrozenAt());
        assertEquals(1, res.getLeaderboards().size());
        assertEquals("SILVER", res.getLeaderboards().get(0).getTier());
    }

    @Test
    void getLastSeasonResult_noSnapshots_throws() {
        when(snapshotManager.resolveLastSeasonNumber()).thenReturn(1);
        when(snapshotManager.findBySeasonNumber(1)).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> clanSeasonService.getLastSeasonResult());
    }
}