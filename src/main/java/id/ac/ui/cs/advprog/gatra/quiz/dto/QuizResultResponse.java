package id.ac.ui.cs.advprog.gatra.quiz.dto;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.quiz.model.QuizAnswer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {
    private Float score;
    private Float passingScore;
    private Boolean passed;
    private List<QuizAnswer> answers;
    private MilestoneResponse milestoneResponse;

    private Double pointsEarned;

    public QuizResultResponse(Float score, Float passingScore, Boolean passed, List<QuizAnswer> answers) {
        this.score = score;
        this.passingScore = passingScore;
        this.passed = passed;
        this.answers = answers;
    }

}