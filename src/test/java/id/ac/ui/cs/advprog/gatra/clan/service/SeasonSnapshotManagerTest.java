package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.SeasonSnapshot;
import id.ac.ui.cs.advprog.gatra.clan.repository.SeasonSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeasonSnapshotManagerTest {

    @Mock
    private SeasonSnapshotRepository snapshotRepository;

    @InjectMocks
    private SeasonSnapshotManager snapshotManager;

    private SeasonSnapshot snapshot1;
    private SeasonSnapshot snapshot2;

    @BeforeEach
    void setUp() {
        snapshot1 = SeasonSnapshot.builder()
                .id("snap-1").clanId("clan-1").clanName("Alpha")
                .tier("GOLD").finalScore(900.0).finalRank(1).seasonNumber(2).build();
        snapshot2 = SeasonSnapshot.builder()
                .id("snap-2").clanId("clan-2").clanName("Beta")
                .tier("SILVER").finalScore(700.0).finalRank(1).seasonNumber(3).build();
    }

    @Test
    void resolveNextSeasonNumber_shouldReturnMaxPlusOne_whenSnapshotsExist() {
        when(snapshotRepository.findAll()).thenReturn(List.of(snapshot1, snapshot2));

        int result = snapshotManager.resolveNextSeasonNumber();

        assertEquals(4, result);
    }

    @Test
    void resolveNextSeasonNumber_shouldReturnOne_whenNoSnapshotsExist() {
        when(snapshotRepository.findAll()).thenReturn(List.of());

        int result = snapshotManager.resolveNextSeasonNumber();

        assertEquals(1, result);
    }

    @Test
    void resolveLastSeasonNumber_shouldReturnMaxSeasonNumber_whenSnapshotsExist() {
        when(snapshotRepository.findAll()).thenReturn(List.of(snapshot1, snapshot2));

        int result = snapshotManager.resolveLastSeasonNumber();

        assertEquals(3, result);
    }

    @Test
    void resolveLastSeasonNumber_shouldThrowException_whenNoSnapshotsExist() {
        when(snapshotRepository.findAll()).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> snapshotManager.resolveLastSeasonNumber());

        assertEquals("Belum ada musim yang selesai.", ex.getMessage());
    }

    @Test
    void saveSnapshots_shouldSaveOneSnapshotPerEntry() {
        LeaderboardEntryResponse entry1 = LeaderboardEntryResponse.builder()
                .clanId("clan-1").clanName("Alpha").tier("GOLD").score(900.0).rank(1).build();
        LeaderboardEntryResponse entry2 = LeaderboardEntryResponse.builder()
                .clanId("clan-2").clanName("Beta").tier("GOLD").score(700.0).rank(2).build();

        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier("GOLD").rankings(List.of(entry1, entry2)).build();

        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 0, 0);

        snapshotManager.saveSnapshots(List.of(tierBoard), 5, now);

        verify(snapshotRepository, times(2)).save(any(SeasonSnapshot.class));
    }

    @Test
    void saveSnapshots_shouldSaveCorrectSnapshotData() {
        LeaderboardEntryResponse entry = LeaderboardEntryResponse.builder()
                .clanId("clan-1").clanName("Alpha").tier("GOLD").score(900.0).rank(1).build();

        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier("GOLD").rankings(List.of(entry)).build();

        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 0, 0);

        snapshotManager.saveSnapshots(List.of(tierBoard), 5, now);

        ArgumentCaptor<SeasonSnapshot> captor = ArgumentCaptor.forClass(SeasonSnapshot.class);
        verify(snapshotRepository).save(captor.capture());

        SeasonSnapshot saved = captor.getValue();
        assertEquals("clan-1", saved.getClanId());
        assertEquals("Alpha", saved.getClanName());
        assertEquals("GOLD", saved.getTier());
        assertEquals(900.0, saved.getFinalScore());
        assertEquals(1, saved.getFinalRank());
        assertEquals(5, saved.getSeasonNumber());
        assertEquals(now, saved.getSnapshotAt());
    }

    @Test
    void saveSnapshots_shouldNotSaveAnything_whenLeaderboardsEmpty() {
        snapshotManager.saveSnapshots(List.of(), 1, LocalDateTime.now());

        verifyNoInteractions(snapshotRepository);
    }

    @Test
    void saveSnapshots_shouldHandleMultipleTierBoards() {
        LeaderboardEntryResponse goldEntry = LeaderboardEntryResponse.builder()
                .clanId("clan-1").clanName("Alpha").tier("GOLD").score(900.0).rank(1).build();
        LeaderboardEntryResponse silverEntry = LeaderboardEntryResponse.builder()
                .clanId("clan-2").clanName("Beta").tier("SILVER").score(600.0).rank(1).build();

        TierLeaderboardResponse goldBoard = TierLeaderboardResponse.builder()
                .tier("GOLD").rankings(List.of(goldEntry)).build();
        TierLeaderboardResponse silverBoard = TierLeaderboardResponse.builder()
                .tier("SILVER").rankings(List.of(silverEntry)).build();

        snapshotManager.saveSnapshots(List.of(goldBoard, silverBoard), 2, LocalDateTime.now());

        verify(snapshotRepository, times(2)).save(any(SeasonSnapshot.class));
    }

    @Test
    void findBySeasonNumber_shouldDelegateToRepository() {
        when(snapshotRepository.findBySeasonNumberOrderByTierAscFinalRankAsc(3))
                .thenReturn(List.of(snapshot1, snapshot2));

        List<SeasonSnapshot> result = snapshotManager.findBySeasonNumber(3);

        assertEquals(2, result.size());
        verify(snapshotRepository, times(1))
                .findBySeasonNumberOrderByTierAscFinalRankAsc(3);
    }

    @Test
    void findBySeasonNumber_shouldReturnEmptyList_whenNoSnapshotsFound() {
        when(snapshotRepository.findBySeasonNumberOrderByTierAscFinalRankAsc(99))
                .thenReturn(List.of());

        List<SeasonSnapshot> result = snapshotManager.findBySeasonNumber(99);

        assertTrue(result.isEmpty());
    }
}