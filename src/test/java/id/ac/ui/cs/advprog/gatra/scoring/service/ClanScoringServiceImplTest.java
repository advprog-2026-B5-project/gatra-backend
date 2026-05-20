package id.ac.ui.cs.advprog.gatra.scoring.service;

import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import id.ac.ui.cs.advprog.gatra.scoring.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClanScoringServiceImplTest {

    @Mock
    private ClanMembershipRepository clanMembershipRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    private ClanScoringServiceImpl clanScoringService;

    @BeforeEach
    void setUp() {
        // Wire all 4 strategies into the service
        List<TierScoringStrategy> strategies = List.of(
                new BronzeScoringStrategy(),
                new SilverScoringStrategy(),
                new GoldScoringStrategy(),
                new DiamondScoringStrategy()
        );
        clanScoringService = new ClanScoringServiceImpl(strategies, clanMembershipRepository, pointHistoryRepository);
    }

    // ==========================================
    // BRONZE TIER TESTS (Summation)
    // ==========================================
    @Test
    void testCalculateClanScore_BronzeTier_NoModifiers() {
        String clanId = "clan-bronze-1";

        when(clanMembershipRepository.countByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(10L);
        when(pointHistoryRepository.sumPointsByClanId(clanId)).thenReturn(1500.0);

        double result = clanScoringService.calculateClanScore(clanId, "BRONZE", List.of());

        // Expected Base Score = 1500.0 (Sum of all points)
        assertEquals(1500.0, result, 0.001, "Bronze score calculated incorrectly");
    }

    // ==========================================
    // SILVER TIER TESTS (Synergy Bonus)
    // ==========================================
    @Test
    void testCalculateClanScore_SilverTier_WithBuff() {
        String clanId = "clan-silver-1";

        when(clanMembershipRepository.countByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(10L);
        when(pointHistoryRepository.sumPointsByClanId(clanId)).thenReturn(1000.0);

        List<ScoreModifier> modifiers = List.of(
                new ScoreModifier("Weekend Double XP", 1.5)
        );

        double result = clanScoringService.calculateClanScore(clanId, "SILVER", modifiers);

        // Expected Base: 1000.0 + (10 members * 50) = 1500.0
        // Expected Final: 1500.0 * 1.5 = 2250.0
        assertEquals(2250.0, result, 0.001, "Silver Synergy score with buff calculated incorrectly");
    }

    // ==========================================
    // GOLD TIER TESTS (Overdrive Quota)
    // ==========================================
    @Test
    void testCalculateClanScore_GoldTier_BelowQuota() {
        String clanId = "clan-gold-under";

        // 5 members -> Quota = 5 * 200 = 1000 points.
        when(clanMembershipRepository.countByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(5L);
        // Earned 800 (Below quota)
        when(pointHistoryRepository.sumPointsByClanId(clanId)).thenReturn(800.0);

        double result = clanScoringService.calculateClanScore(clanId, "GOLD", List.of());

        // Expected Base Score: 800.0 (No overdrive multiplier applied)
        assertEquals(800.0, result, 0.001, "Gold score below quota calculated incorrectly");
    }

    @Test
    void testCalculateClanScore_GoldTier_OverdriveActivated() {
        String clanId = "clan-gold-overdrive";

        // 5 members -> Quota = 5 * 200 = 1000 points.
        when(clanMembershipRepository.countByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(5L);
        // Earned 1400 (400 points into Overdrive)
        when(pointHistoryRepository.sumPointsByClanId(clanId)).thenReturn(1400.0);

        double result = clanScoringService.calculateClanScore(clanId, "GOLD", List.of());

        // Expected Math: 1000 (Base) + (400 * 1.5) = 1000 + 600 = 1600.0
        assertEquals(1600.0, result, 0.001, "Gold Overdrive score calculated incorrectly");
    }

    // ==========================================
    // DIAMOND TIER TESTS (Weighted Average)
    // ==========================================
    @Test
    void testCalculateClanScore_DiamondTier_WithStackingModifiers() {
        String clanId = "clan-diamond-1";

        when(clanMembershipRepository.countByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(5L);
        when(pointHistoryRepository.sumPointsByClanId(clanId)).thenReturn(1000.0);

        // Stacking Modifiers: 1.2x (Buff) and 0.8x (Penalty)
        List<ScoreModifier> modifiers = List.of(
                new ScoreModifier("Productivity Buff", 1.2),
                new ScoreModifier("Low Accuracy Penalty", 0.8)
        );

        double result = clanScoringService.calculateClanScore(clanId, "DIAMOND", modifiers);

        // Expected Base (Diamond): 1000 / 5 = 200.0
        // Buff applied: 200.0 * 1.2 = 240.0
        // Penalty applied: 240.0 * 0.8 = 192.0
        assertEquals(192.0, result, 0.001, "Diamond score with stacking modifiers calculated incorrectly");
    }

    @Test
    void testCalculateClanScore_DiamondTier_ZeroMembers() {
        String clanId = "clan-diamond-empty";

        // Edge case: Prevent division by zero
        when(clanMembershipRepository.countByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(0L);
        when(pointHistoryRepository.sumPointsByClanId(clanId)).thenReturn(500.0);

        double result = clanScoringService.calculateClanScore(clanId, "DIAMOND", List.of());

        // Expected Base: 0.0 (Handled safely inside the strategy to return 0.0 without crashing)
        assertEquals(0.0, result, 0.001, "Diamond score should be 0 when there are no approved members");
    }

    // ==========================================
    // SYSTEM EDGE CASES
    // ==========================================
    @Test
    void testCalculateClanScore_InvalidTier_ThrowsException() {
        String clanId = "clan-unknown";

        when(clanMembershipRepository.countByClanIdAndStatus(clanId, MembershipStatus.APPROVED)).thenReturn(5L);
        when(pointHistoryRepository.sumPointsByClanId(clanId)).thenReturn(500.0);

        // Assert that passing an unsupported tier name throws the proper exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clanScoringService.calculateClanScore(clanId, "PLATINUM", List.of());
        });

        assertEquals("Unsupported clan tier: PLATINUM", exception.getMessage());
    }
}