package id.ac.ui.cs.advprog.gatra.quiz.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassingScoreRequest {

    @Min(value = 0, message = "Passing score cannot be below 0")
    @Max(value = 100, message = "Passing score cannot exceed 100")
    private Integer passingScore = 50;
}