package id.ac.ui.cs.advprog.gatra.quiz.model;

import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.dto.UpdateQuestionRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Question {

    @Id
    @GeneratedValue
    private UUID id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    private String correctAnswer;

    public abstract void applyCreate(CreateQuestionRequest request);
    public abstract void applyUpdate(UpdateQuestionRequest request);
    public boolean checkAnswer(String userAnswer) {
        return this.correctAnswer.equalsIgnoreCase(userAnswer);
    }
}