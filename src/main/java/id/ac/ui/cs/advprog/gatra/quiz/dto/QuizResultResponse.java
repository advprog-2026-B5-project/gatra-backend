package id.ac.ui.cs.advprog.gatra.quiz.dto;

import id.ac.ui.cs.advprog.gatra.quiz.model.QuizAnswer;

import java.util.List;

public class QuizResultResponse {
    private Float score;
    private Float passingScore;
    private Boolean passed;
    private List<QuizAnswer> answers;

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
}
