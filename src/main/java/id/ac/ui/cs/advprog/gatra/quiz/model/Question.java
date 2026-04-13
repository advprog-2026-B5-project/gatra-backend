package id.ac.ui.cs.advprog.gatra.quiz.model;

import id.ac.ui.cs.advprog.gatra.model.Article;
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
}