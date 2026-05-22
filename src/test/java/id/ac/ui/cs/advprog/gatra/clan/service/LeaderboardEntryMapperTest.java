package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.decorator.ScoreCalculator;
import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardEntryMapperTest {

    @Mock
    private BuffDebuffService buffDebuffService;

    @Mock
    private ScoreCalculator scoreCalculator;

    @InjectMocks
    private LeaderboardEntryMapper leaderboardEntryMapper;

    private Clan clan;

    @BeforeEach
    void setUp() {
        clan = Clan.builder()
                .id("clan-123")
                .name("Shadow Guild")
                .tier("GOLD")
                .build();
    }

    @Test
    void toEntry_shouldReturnCorrectClanIdAndName() {
        when(buffDebuffService.buildCalculator("clan-123")).thenReturn(scoreCalculator);
        when(scoreCalculator.calculate("clan-123", "GOLD")).thenReturn(850.0);

        LeaderboardEntryResponse result = leaderboardEntryMapper.toEntry(clan);

        assertEquals("clan-123", result.getClanId());
        assertEquals("Shadow Guild", result.getClanName());
    }

    @Test
    void toEntry_shouldReturnCorrectTier() {
        when(buffDebuffService.buildCalculator("clan-123")).thenReturn(scoreCalculator);
        when(scoreCalculator.calculate("clan-123", "GOLD")).thenReturn(850.0);

        LeaderboardEntryResponse result = leaderboardEntryMapper.toEntry(clan);

        assertEquals("GOLD", result.getTier());
    }

    @Test
    void toEntry_shouldReturnScoreFromCalculator() {
        when(buffDebuffService.buildCalculator("clan-123")).thenReturn(scoreCalculator);
        when(scoreCalculator.calculate("clan-123", "GOLD")).thenReturn(850.0);

        LeaderboardEntryResponse result = leaderboardEntryMapper.toEntry(clan);

        assertEquals(850.0, result.getScore());
    }

    @Test
    void toEntry_shouldCallBuildCalculatorWithClanId() {
        when(buffDebuffService.buildCalculator("clan-123")).thenReturn(scoreCalculator);
        when(scoreCalculator.calculate("clan-123", "GOLD")).thenReturn(850.0);

        leaderboardEntryMapper.toEntry(clan);

        verify(buffDebuffService, times(1)).buildCalculator("clan-123");
    }

    @Test
    void toEntry_shouldCallCalculateWithClanIdAndTier() {
        when(buffDebuffService.buildCalculator("clan-123")).thenReturn(scoreCalculator);
        when(scoreCalculator.calculate("clan-123", "GOLD")).thenReturn(850.0);

        leaderboardEntryMapper.toEntry(clan);

        verify(scoreCalculator, times(1)).calculate("clan-123", "GOLD");
    }

    @Test
    void toEntry_shouldReturnZeroScore_whenCalculatorReturnsZero() {
        when(buffDebuffService.buildCalculator("clan-123")).thenReturn(scoreCalculator);
        when(scoreCalculator.calculate("clan-123", "GOLD")).thenReturn(0.0);

        LeaderboardEntryResponse result = leaderboardEntryMapper.toEntry(clan);

        assertEquals(0.0, result.getScore());
    }

    @Test
    void toEntry_shouldHandleNegativeScore() {
        when(buffDebuffService.buildCalculator("clan-123")).thenReturn(scoreCalculator);
        when(scoreCalculator.calculate("clan-123", "GOLD")).thenReturn(-100.0);

        LeaderboardEntryResponse result = leaderboardEntryMapper.toEntry(clan);

        assertEquals(-100.0, result.getScore());
    }

    @Test
    void toEntry_shouldWorkWithBronzeTier() {
        Clan bronzeClan = Clan.builder()
                .id("clan-456")
                .name("Iron Wolves")
                .tier("BRONZE")
                .build();

        when(buffDebuffService.buildCalculator("clan-456")).thenReturn(scoreCalculator);
        when(scoreCalculator.calculate("clan-456", "BRONZE")).thenReturn(100.0);

        LeaderboardEntryResponse result = leaderboardEntryMapper.toEntry(bronzeClan);

        assertEquals("BRONZE", result.getTier());
        assertEquals(100.0, result.getScore());
    }
}