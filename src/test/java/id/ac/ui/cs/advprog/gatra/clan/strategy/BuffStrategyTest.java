package id.ac.ui.cs.advprog.gatra.clan.strategy;

import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuffStrategyTest {
    private BuffStrategy buffStrategy;

    @BeforeEach
    void setUp() {
        buffStrategy = new BuffStrategy();
    }

    @Test
    void isApplicable_returnsTrue_whenCompletionRateIsAtLeastThreshold() {
        assertTrue(buffStrategy.isApplicable(0.5));
        assertTrue(buffStrategy.isApplicable(0.75));
        assertTrue(buffStrategy.isApplicable(1.0));
    }

    @Test
    void isApplicable_returnsFalse_whenCompletionRateIsBelowThreshold() {
        assertFalse(buffStrategy.isApplicable(0.49));
        assertFalse(buffStrategy.isApplicable(0.0));
    }

    @Test
    void getModifier_returnsCorrectModifier() {
        ScoreModifier modifier = buffStrategy.getModifier();
        assertEquals("BUFF", modifier.getModifierName());
        assertEquals(1.2, modifier.getMultiplier());
    }
}