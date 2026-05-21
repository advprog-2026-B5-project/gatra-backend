package id.ac.ui.cs.advprog.gatra.clan.decorator;

public class DebuffDecorator extends ScoreModifierDecorator {
    private static final double MULTIPLIER = 0.8;

    public DebuffDecorator(ScoreCalculator wrapped) {
        super(wrapped);
    }

    @Override
    protected double applyModifier(double score) {
        return score * MULTIPLIER;
    }
}