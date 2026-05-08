package id.ac.ui.cs.advprog.gatra.clan.strategy;

import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class DebuffStrategy implements BuffDebuffStrategy {

    private static final double MULTIPLIER = 0.8;

    @Override
    public boolean isApplicable(double completionRate) {
        return true;
    }

    @Override
    public ScoreModifier getModifier() {
        return new ScoreModifier(ModifierType.DEBUFF.name(), MULTIPLIER);
    }
}