package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanTier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TierMigrationProcessorTest {

    @Mock
    private TierResolver tierResolver;

    @Mock
    private ClanTierUpdater clanTierUpdater;

    @InjectMocks
    private TierMigrationProcessor tierMigrationProcessor;

    @Test
    void processTierMigration_shouldCallResolveForEachClan() {
        LeaderboardEntryResponse entry1 = LeaderboardEntryResponse.builder()
                .clanId("clan-1").rank(1).tier("GOLD").build();
        LeaderboardEntryResponse entry2 = LeaderboardEntryResponse.builder()
                .clanId("clan-2").rank(2).tier("GOLD").build();
        LeaderboardEntryResponse entry3 = LeaderboardEntryResponse.builder()
                .clanId("clan-3").rank(3).tier("GOLD").build();

        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier("GOLD").rankings(List.of(entry1, entry2, entry3)).build();

        when(tierResolver.resolveNewTier(any(), anyInt(), anyInt()))
                .thenReturn(ClanTier.GOLD);

        tierMigrationProcessor.processTierMigration(tierBoard);

        verify(tierResolver, times(3)).resolveNewTier(eq(ClanTier.GOLD), anyInt(), eq(3));
    }

    @Test
    void processTierMigration_shouldCallUpdateClanTierForEachClan() {
        LeaderboardEntryResponse entry1 = LeaderboardEntryResponse.builder()
                .clanId("clan-1").rank(1).tier("GOLD").build();
        LeaderboardEntryResponse entry2 = LeaderboardEntryResponse.builder()
                .clanId("clan-2").rank(2).tier("GOLD").build();

        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier("GOLD").rankings(List.of(entry1, entry2)).build();

        when(tierResolver.resolveNewTier(ClanTier.GOLD, 1, 2)).thenReturn(ClanTier.DIAMOND);
        when(tierResolver.resolveNewTier(ClanTier.GOLD, 2, 2)).thenReturn(ClanTier.SILVER);

        tierMigrationProcessor.processTierMigration(tierBoard);

        verify(clanTierUpdater, times(1)).updateClanTier("clan-1", ClanTier.DIAMOND);
        verify(clanTierUpdater, times(1)).updateClanTier("clan-2", ClanTier.SILVER);
    }

    @Test
    void processTierMigration_shouldPassCorrectRankToResolver() {
        LeaderboardEntryResponse entry1 = LeaderboardEntryResponse.builder()
                .clanId("clan-1").rank(1).tier("SILVER").build();
        LeaderboardEntryResponse entry2 = LeaderboardEntryResponse.builder()
                .clanId("clan-2").rank(2).tier("SILVER").build();

        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier("SILVER").rankings(List.of(entry1, entry2)).build();

        when(tierResolver.resolveNewTier(any(), anyInt(), anyInt()))
                .thenReturn(ClanTier.SILVER);

        tierMigrationProcessor.processTierMigration(tierBoard);

        verify(tierResolver).resolveNewTier(ClanTier.SILVER, 1, 2);
        verify(tierResolver).resolveNewTier(ClanTier.SILVER, 2, 2);
    }

    @Test
    void processTierMigration_shouldDoNothing_whenRankingsEmpty() {
        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier("BRONZE").rankings(List.of()).build();

        tierMigrationProcessor.processTierMigration(tierBoard);

        verifyNoInteractions(tierResolver);
        verifyNoInteractions(clanTierUpdater);
    }

    @Test
    void processTierMigration_shouldHandleSingleClan() {
        LeaderboardEntryResponse entry = LeaderboardEntryResponse.builder()
                .clanId("clan-1").rank(1).tier("BRONZE").build();

        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier("BRONZE").rankings(List.of(entry)).build();

        when(tierResolver.resolveNewTier(ClanTier.BRONZE, 1, 1)).thenReturn(ClanTier.SILVER);

        tierMigrationProcessor.processTierMigration(tierBoard);

        verify(tierResolver, times(1)).resolveNewTier(ClanTier.BRONZE, 1, 1);
        verify(clanTierUpdater, times(1)).updateClanTier("clan-1", ClanTier.SILVER);
    }
}