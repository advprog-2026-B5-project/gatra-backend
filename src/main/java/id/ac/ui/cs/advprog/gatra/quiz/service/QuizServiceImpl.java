package id.ac.ui.cs.advprog.gatra.quiz.service;
import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.article.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.dto.UpdateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuestionRepository;
import id.ac.ui.cs.advprog.gatra.quiz.Monitoring.MonitoringQuestion;
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
    private final MonitoringQuestion monitoringQuestion;

    @Override
    public Question createQuestion(CreateQuestionRequest request) {
        Article article = findArticleOrThrow(request.getArticleId());

        Question question = questionFactory.create(request.getType());
        question.applyCreate(request);
        question.setArticle(article);

        Question savedQuestion = questionRepository.save(question);
        monitoringQuestion.incrementQuestionCreated();
        return savedQuestion;
    }

    @Override
    public Question updateQuestion(UUID id, UpdateQuestionRequest request) {
        Question question = findQuestionOrThrow(id);

        question.applyUpdate(request);
        Question updatedQuestion = questionRepository.save(question);
        monitoringQuestion.incrementQuestionUpdated();
        return updatedQuestion;
    }

    @Override
    public void deleteQuestion(UUID id) {
        findQuestionOrThrow(id);
        questionRepository.deleteById(id);
        monitoringQuestion.incrementQuestionDeleted();
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question getQuestionById(UUID id) {
        return findQuestionOrThrow(id);
    }

    @Override
    public List<Question> getQuestionsByArticle(UUID articleId) {
        return questionRepository.findByArticleId(articleId);
    }


    @Override
    public void setPassingScore(UUID articleId, Integer passingScore) {
        Article article = findArticleOrThrow(articleId);

        article.setPassingScore(passingScore);
        articleRepository.save(article);
        monitoringQuestion.incrementPassingScoreUpdated();
    }

    private Article findArticleOrThrow(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
    }

    private Question findQuestionOrThrow(UUID id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", id));
    }
}