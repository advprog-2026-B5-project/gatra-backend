package id.ac.ui.cs.advprog.gatra.quiz.model;

import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.dto.UpdateQuestionRequest;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TrueFalseQuestion extends Question {
    private String correctAnswer;

    @Override
    public void applyCreate(CreateQuestionRequest request){
        this.setText(request.getText());
        this.setCorrectAnswer(request.getCorrectAnswer());
    }

    @Override
    public void applyUpdate(UpdateQuestionRequest request){
        this.setText(request.getText());
        this.setCorrectAnswer(request.getCorrectAnswer());
    }

}