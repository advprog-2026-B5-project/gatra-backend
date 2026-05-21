package id.ac.ui.cs.advprog.gatra.quiz.controller;

import id.ac.ui.cs.advprog.gatra.quiz.dto.*;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.service.QuizAttemptService;
import id.ac.ui.cs.advprog.gatra.quiz.service.QuizService;
import id.ac.ui.cs.advprog.gatra.quiz.mapper.QuestionMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final QuestionMapper questionMapper;

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody @Valid CreateQuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionMapper.toResponse(quizService.createQuestion(request)));
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        List<QuestionResponse> responses = quizService.getAllQuestions().stream()
                .map(questionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable UUID id) {
        return ResponseEntity.ok(questionMapper.toResponse(quizService.getQuestionById(id)));
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByArticle(@PathVariable UUID articleId) {
        List<QuestionResponse> responses = quizService.getQuestionsByArticle(articleId).stream()
                .map(questionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        quizService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateQuestionRequest request) {
        return ResponseEntity.ok(questionMapper.toResponse(quizService.updateQuestion(id, request)));
    }

    @PatchMapping("/passing-score/{articleId}")
    public ResponseEntity<Void> setPassingScore(
            @PathVariable UUID articleId,
            @RequestBody @Valid PassingScoreRequest request) {
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