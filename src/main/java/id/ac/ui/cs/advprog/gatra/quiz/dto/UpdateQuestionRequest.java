package id.ac.ui.cs.advprog.gatra.quiz.dto;

import java.util.List;

public class UpdateQuestionRequest {

    private String text;
    private List<String> options;
    private String correctAnswer;

    public UpdateQuestionRequest() {}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}