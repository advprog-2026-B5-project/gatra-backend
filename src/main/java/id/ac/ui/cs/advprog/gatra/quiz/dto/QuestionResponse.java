package id.ac.ui.cs.advprog.gatra.quiz.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {
    private UUID id;
    private String type;
    private String text;
    private UUID articleId;
    private List<String> options;
    private String correctAnswer;
}