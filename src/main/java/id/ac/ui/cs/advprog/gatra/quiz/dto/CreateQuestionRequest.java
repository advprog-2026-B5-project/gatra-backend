package id.ac.ui.cs.advprog.gatra.quiz.dto;

import java.util.List;
import java.util.UUID;

public class CreateQuestionRequest {
    private String type; // multiple choice or true false
    private String text;
    private List<String> options;
    private String correctAnswer;
    private UUID articleId;

    public CreateQuestionRequest(String text, List<String> options, String correctAnswer, UUID articleId) {
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.articleId = articleId;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public UUID getArticleId() {
        return articleId;
    }

    public String getType(){return  type;}
}