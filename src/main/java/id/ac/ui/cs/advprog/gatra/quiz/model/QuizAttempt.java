package id.ac.ui.cs.advprog.gatra.quiz.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quiz_attempts")
@Getter
@Setter
public class QuizAttempt {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "article_id")
    private UUID articleId;

    private Integer score;
    private Boolean passed;

    @CreationTimestamp
    private LocalDateTime attemptedAt;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    private List<QuizAnswer> answers;
}