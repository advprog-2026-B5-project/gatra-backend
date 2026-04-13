package id.ac.ui.cs.advprog.gatra.quiz.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class MultipleChoiceQuestion extends Question {

    @ElementCollection
    private List<String> options;

    private String correctAnswer;
}