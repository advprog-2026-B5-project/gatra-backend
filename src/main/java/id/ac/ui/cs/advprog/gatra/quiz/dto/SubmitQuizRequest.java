package id.ac.ui.cs.advprog.gatra.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SubmitQuizRequest {

    @NotNull(message = "Article ID is required")
    private UUID articleId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @Valid
    @NotEmpty(message = "Answers cannot be empty")
    private List<AnswerItem> answers;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AnswerItem {

        @NotNull(message = "Question ID is required")
        private UUID questionId;

        @NotBlank(message = "Answer is required")
        private String answer;
    }
}