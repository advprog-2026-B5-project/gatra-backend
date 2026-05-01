package id.ac.ui.cs.advprog.gatra.quiz.dto;

import id.ac.ui.cs.advprog.gatra.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.quiz.model.QuizAnswer;
import java.util.List;

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

    public Float getScore() { return score; }
    public Float getPassingScore() { return passingScore; }
    public Boolean getPassed() { return passed; }
    public List<QuizAnswer> getAnswers() { return answers; }

    public MilestoneResponse getMilestoneResponse() { return milestoneResponse; }
    public void setMilestoneResponse(MilestoneResponse milestoneResponse) { this.milestoneResponse = milestoneResponse; }

    public Double getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(Double pointsEarned) { this.pointsEarned = pointsEarned; }
}