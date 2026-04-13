package id.ac.ui.cs.advprog.gatra.quiz.controller;

import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.service.QuizService;
import id.ac.ui.cs.advprog.gatra.quiz.dto.UpdateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.dto.PassingScoreRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

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


}