package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;

import java.util.List;
import java.util.UUID;

public interface QuizService {

    Question createQuestion(CreateQuestionRequest request);

    List<Question> getAllQuestions();

    Question getQuestionById(UUID id);

    void deleteQuestion(UUID id);

    List<Question> getQuestionsByArticle(UUID articleId);
}