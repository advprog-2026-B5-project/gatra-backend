package id.ac.ui.cs.advprog.gatra.clan.decorator;

public class BuffDecorator extends ScoreModifierDecorator {
    private static final double MULTIPLIER = 1.2;

    public BuffDecorator(ScoreCalculator wrapped) {
        super(wrapped);
    }

    @Override
    protected double applyModifier(double score) {
        return score * MULTIPLIER;
    }

    @Override
    public String getModifierName() { return "Productivity Buff (×1.2)"; }
}
