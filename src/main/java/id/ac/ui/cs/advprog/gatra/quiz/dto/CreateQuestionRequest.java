package id.ac.ui.cs.advprog.gatra.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateQuestionRequest {
    private String type; // multiple choice or true false
    @NotBlank(message = "Question text is required")
    private String text;

    private List<String> options;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    @NotNull(message = "Article ID is required")
    private UUID articleId;

    public CreateQuestionRequest(String text, List<String> options, String correctAnswer, UUID articleId) {
        this.text = text;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.articleId = articleId;
    }
}