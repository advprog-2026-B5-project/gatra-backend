package id.ac.ui.cs.advprog.gatra.quiz.model;

import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.dto.UpdateQuestionRequest;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class MultipleChoiceQuestion extends Question {

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> options;

    private String correctAnswer;

    @Override
    public void applyCreate(CreateQuestionRequest request){
        this.setText(request.getText());
        this.setOptions(request.getOptions());
        this.setCorrectAnswer(request.getCorrectAnswer());
    }

    @Override
    public void applyUpdate(UpdateQuestionRequest request){
        this.setText(request.getText());
        this.setOptions(request.getOptions());
        this.setCorrectAnswer(request.getCorrectAnswer());
    }
}