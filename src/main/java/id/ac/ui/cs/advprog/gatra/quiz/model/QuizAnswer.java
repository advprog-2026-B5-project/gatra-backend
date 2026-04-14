package id.ac.ui.cs.advprog.gatra.quiz.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "quiz_answers")
@Getter
@Setter
public class QuizAnswer {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private QuizAttempt attempt;

    @Column(name = "question_id")
    private UUID questionId;

    private String userAnswer;
    private Boolean isCorrect;
}