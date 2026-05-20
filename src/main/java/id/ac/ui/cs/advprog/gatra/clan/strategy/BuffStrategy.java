package id.ac.ui.cs.advprog.gatra.clan.strategy;

import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class BuffStrategy implements BuffDebuffStrategy {

    private static final double THRESHOLD = 0.5;
    private static final double MULTIPLIER = 1.2;

    @Override
    public boolean isApplicable(double completionRate) {
        return completionRate >= THRESHOLD;
    }

    @Override
    public ScoreModifier getModifier() {
        return new ScoreModifier(ModifierType.BUFF.name(), MULTIPLIER);
    }
}