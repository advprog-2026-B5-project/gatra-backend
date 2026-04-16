package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.gatra.quiz.dto.SubmitQuizRequest;
import java.util.UUID;

public interface QuizAttemptService {
    QuizResultResponse submitQuiz(SubmitQuizRequest request);
    boolean hasUserPassed(UUID userId, UUID articleId);
}