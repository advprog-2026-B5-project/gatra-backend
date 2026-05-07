package id.ac.ui.cs.advprog.gatra.scoring.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScoreModifier {
    private String modifierName;
    private double multiplier;

    public double apply(double currentScore) {
        return currentScore * multiplier;
    }
}