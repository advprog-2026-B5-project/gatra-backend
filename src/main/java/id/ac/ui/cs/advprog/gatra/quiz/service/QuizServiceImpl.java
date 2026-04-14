package id.ac.ui.cs.advprog.gatra.quiz.service;
import id.ac.ui.cs.advprog.gatra.model.Article;
import id.ac.ui.cs.advprog.gatra.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.dto.UpdateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.MultipleChoiceQuestion;
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

    private final ArticleRepository articleRepository;
    private final QuestionFactory questionFactory;

    @Override
    public Question createQuestion(CreateQuestionRequest request) {
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new RuntimeException("Article not found"));

        Question question = questionFactory.create(request.getType());
        question.applyCreate(request);
        question.setArticle(article);
        return questionRepository.save(question);
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

    @Override
    public List<Question> getQuestionsByArticle(UUID articleId) {
        return questionRepository.findByArticleId(articleId);
    }

    @Override
    public Question updateQuestion(UUID id, UpdateQuestionRequest request) {
        Question question = (MultipleChoiceQuestion) questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.applyUpdate(request);

        return questionRepository.save(question);
    }

    @Override
    public void setPassingScore(UUID articleId, Integer passingScore) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        article.setPassingScore(passingScore);
        articleRepository.save(article);
    }
}