package id.ac.ui.cs.advprog.gatra.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateQuestionRequest {
    private String type; // multiple choice or true false

    @NotBlank(message = "Question text is required")
    private String text;

    private List<String> options;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;
}