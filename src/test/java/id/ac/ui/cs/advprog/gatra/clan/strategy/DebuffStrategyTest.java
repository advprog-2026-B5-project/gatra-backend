package id.ac.ui.cs.advprog.gatra.clan.strategy;

import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebuffStrategyTest {
    private DebuffStrategy debuffStrategy;

    @BeforeEach
    void setUp() {
        debuffStrategy = new DebuffStrategy();
    }

    @Test
    void isApplicable_alwaysReturnsTrue() {
        assertTrue(debuffStrategy.isApplicable(0.0));
        assertTrue(debuffStrategy.isApplicable(0.49));
        assertTrue(debuffStrategy.isApplicable(1.0));
    }

    @Test
    void getModifier_returnsCorrectModifier() {
        ScoreModifier modifier = debuffStrategy.getModifier();
        assertEquals("DEBUFF", modifier.getModifierName());
        assertEquals(0.8, modifier.getMultiplier());
    }
}