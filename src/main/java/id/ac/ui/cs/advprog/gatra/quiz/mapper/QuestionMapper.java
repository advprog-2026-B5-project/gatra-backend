package id.ac.ui.cs.advprog.gatra.quiz.mapper;

import id.ac.ui.cs.advprog.gatra.quiz.dto.QuestionResponse;
import id.ac.ui.cs.advprog.gatra.quiz.model.MultipleChoiceQuestion;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.model.TrueFalseQuestion;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {
    public QuestionResponse toResponse(Question question) {
        QuestionResponse.QuestionResponseBuilder builder = QuestionResponse.builder()
                .id(question.getId())
                .text(question.getText())
                .articleId(question.getArticle().getId());

        if (question instanceof MultipleChoiceQuestion mcq) {
            builder.type("MULTIPLE_CHOICE")
                    .options(mcq.getOptions())
                    .correctAnswer(mcq.getCorrectAnswer());
        } else if (question instanceof TrueFalseQuestion tfq) {
            builder.type("TRUE_FALSE")
                    .correctAnswer(tfq.getCorrectAnswer());
        }

        return builder.build();
    }
}