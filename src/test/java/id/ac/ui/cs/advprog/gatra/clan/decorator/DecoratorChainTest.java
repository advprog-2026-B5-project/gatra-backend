package id.ac.ui.cs.advprog.gatra.clan.decorator;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DecoratorChainTest {
    private final ScoreCalculator base = (clanId, tier) -> 100.0;

    @Test
    void noModifier_baseScoreUnchanged() {
        double score = base.calculate("clan-1", "BRONZE");
        assertThat(score).isEqualTo(100.0);
    }

    @Test
    void buffActive_scoreIncrease() {
        ScoreCalculator calc = new BuffDecorator(base);
        assertThat(calc.calculate("clan-1", "BRONZE")).isEqualTo(120.0); // naik ×1.2
    }

    @Test
    void debuffActive_scoreDecrease() {
        // Simulasi: akurasi quiz < 50% → debuff aktif
        ScoreCalculator calc = new DebuffDecorator(base);
        assertThat(calc.calculate("clan-1", "BRONZE")).isEqualTo(80.0); // turun ×0.8
    }

    @Test
    void buffAndDebuff_bothStack() {
        // Simulasi: kedua kondisi aktif → stackable
        ScoreCalculator calc = new DebuffDecorator(new BuffDecorator(base));
        assertThat(calc.calculate("clan-1", "BRONZE")).isEqualTo(96.0); // 100 × 1.2 × 0.8
    }

    @Test
    void scoreCanIncrease_withBuff() {
        ScoreCalculator calc = new BuffDecorator(base);
        assertThat(calc.calculate("clan-1", "BRONZE")).isGreaterThan(100.0);
    }
}