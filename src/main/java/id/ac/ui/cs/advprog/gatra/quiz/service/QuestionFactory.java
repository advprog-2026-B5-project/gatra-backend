package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.quiz.model.*;
import org.springframework.stereotype.Component;

@Component
public class QuestionFactory {
    public Question create(String type) {
        return switch (type.toUpperCase()) {
            case "MULTIPLE_CHOICE" -> new MultipleChoiceQuestion();
            case "TRUE_FALSE"      -> new TrueFalseQuestion();
            default -> throw new IllegalArgumentException("Unknown question type: " + type);
        };
    }
}