package id.ac.ui.cs.advprog.gatra.clan.decorator;

public abstract class ScoreModifierDecorator implements ScoreCalculator {
    protected final ScoreCalculator wrapped;

    protected ScoreModifierDecorator(ScoreCalculator wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public double calculate(String clanId, String tier) {
        return applyModifier(wrapped.calculate(clanId, tier));
    }

    protected abstract double applyModifier(double score);

}
