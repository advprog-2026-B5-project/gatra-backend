package id.ac.ui.cs.advprog.gatra.quiz.controller;

import id.ac.ui.cs.advprog.gatra.quiz.dto.*;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.service.QuizAttemptService;
import id.ac.ui.cs.advprog.gatra.quiz.service.QuizService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor

public class QuizController {

    private final QuizService quizService;
    private final QuizAttemptService quizAttemptService;

    @PostMapping
    public ResponseEntity<Question> createQuestion(
            @RequestBody CreateQuestionRequest request
    ) {
        return ResponseEntity.ok(quizService.createQuestion(request));
    }

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(quizService.getAllQuestions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable UUID id) {
        return ResponseEntity.ok(quizService.getQuestionById(id));
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<Question>> getQuestionsByArticle(@PathVariable UUID articleId) {
        return ResponseEntity.ok(quizService.getQuestionsByArticle(articleId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        quizService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable UUID id,
            @RequestBody UpdateQuestionRequest request
    ) {
        return ResponseEntity.ok(quizService.updateQuestion(id, request));
    }

    @PatchMapping("/passing-score/{articleId}")
    public ResponseEntity<Void> setPassingScore(
            @PathVariable UUID articleId,
            @RequestBody PassingScoreRequest request
    ) {
        quizService.setPassingScore(articleId, request.getPassingScore());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/attempt")
    public ResponseEntity<QuizResultResponse> submitQuiz(@RequestBody SubmitQuizRequest request) {
        return ResponseEntity.ok(quizAttemptService.submitQuiz(request));
    }

    @GetMapping("/attempt/status")
    public ResponseEntity<Boolean> checkStatus(@RequestParam UUID userId, @RequestParam UUID articleId) {
        return ResponseEntity.ok(quizAttemptService.hasUserPassed(userId, articleId));
    }


}