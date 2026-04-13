package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuestionRepository questionRepository;

    @Override
    public Question createQuestion(CreateQuestionRequest request) {
        return null;
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question getQuestionById(UUID id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    @Override
    public void deleteQuestion(UUID id) {
        questionRepository.deleteById(id);
    }
}