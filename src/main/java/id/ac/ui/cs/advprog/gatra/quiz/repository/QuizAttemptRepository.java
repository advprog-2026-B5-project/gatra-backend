package id.ac.ui.cs.advprog.gatra.quiz.repository;

import id.ac.ui.cs.advprog.gatra.quiz.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    List<QuizAttempt> findByUserIdAndArticleId(UUID userId, UUID articleId);
    boolean existsByUserIdAndArticleIdAndPassedTrue(UUID userId, UUID articleId);
    List<QuizAttempt> findByUserId(UUID userId);
}