package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.model.ClanTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TierResolverTest {

    private TierResolver tierResolver;

    @BeforeEach
    void setUp() {
        tierResolver = new TierResolver();
    }

    @Test
    void resolveNewTier_shouldPromote_whenRankOne() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.BRONZE, 1, 10);
        assertEquals(ClanTier.SILVER, result);
    }

    @Test
    void resolveNewTier_shouldPromote_whenRankTwo() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.BRONZE, 2, 10);
        assertEquals(ClanTier.SILVER, result);
    }

    @Test
    void resolveNewTier_shouldPromote_whenRankThree() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.SILVER, 3, 10);
        assertEquals(ClanTier.GOLD, result);
    }

    @Test
    void resolveNewTier_shouldNotPromoteBeyondHighestTier() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.DIAMOND, 1, 10);
        assertEquals(ClanTier.DIAMOND, result);
    }

    @Test
    void resolveNewTier_shouldRelegate_whenRankIsLastThree() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.GOLD, 8, 10);
        assertEquals(ClanTier.SILVER, result);
    }

    @Test
    void resolveNewTier_shouldRelegate_whenRankIsLast() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.SILVER, 10, 10);
        assertEquals(ClanTier.BRONZE, result);
    }

    @Test
    void resolveNewTier_shouldNotRelegateBelowLowestTier() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.BRONZE, 10, 10);
        assertEquals(ClanTier.BRONZE, result);
    }

    @Test
    void resolveNewTier_shouldStay_whenRankInMiddle() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.GOLD, 4, 10);
        assertEquals(ClanTier.GOLD, result);
    }

    @Test
    void resolveNewTier_shouldStay_whenRankExactlyAtRelegationBoundary() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.SILVER, 7, 10);
        assertEquals(ClanTier.SILVER, result);
    }

    @Test
    void resolveNewTier_shouldStay_whenRankFourAndTotalFour() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.GOLD, 4, 4);
        assertEquals(ClanTier.SILVER, result);
    }


    @Test
    void resolveNewTier_shouldPrioritizePromotion_whenTotalClansLessThanSix() {
        ClanTier result = tierResolver.resolveNewTier(ClanTier.SILVER, 3, 5);
        assertEquals(ClanTier.GOLD, result);
    }
}