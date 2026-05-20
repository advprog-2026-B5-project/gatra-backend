package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.article.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.dto.UpdateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.model.TrueFalseQuestion;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private QuestionFactory questionFactory;

    @InjectMocks
    private QuizServiceImpl quizService;

    private Article dummyArticle;
    private TrueFalseQuestion dummyQuestion;
    private UUID articleId;
    private UUID questionId;

    @BeforeEach
    void setUp() {
        articleId = UUID.randomUUID();
        questionId = UUID.randomUUID();

        dummyArticle = new Article();
        dummyArticle.setId(articleId);
        dummyArticle.setPassingScore(50);

        dummyQuestion = new TrueFalseQuestion();
        dummyQuestion.setId(questionId);
        dummyQuestion.setText("Test Question");
        dummyQuestion.setCorrectAnswer("True");
        dummyQuestion.setArticle(dummyArticle);
    }

    @Test
    void createQuestion_shouldReturnSavedQuestion() {
        CreateQuestionRequest request = new CreateQuestionRequest("Test Question", null, "True", articleId);
        
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(dummyArticle));
        when(questionFactory.create(request.getType())).thenReturn(new TrueFalseQuestion());
        when(questionRepository.save(any(Question.class))).thenReturn(dummyQuestion);

        Question result = quizService.createQuestion(request);

        assertNotNull(result);
        assertEquals("Test Question", result.getText());
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void createQuestion_shouldThrowExceptionWhenArticleNotFound() {
        CreateQuestionRequest request = new CreateQuestionRequest("Test Question", null, "True", articleId);
        
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizService.createQuestion(request));
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void getAllQuestions_shouldReturnQuestionList() {
        when(questionRepository.findAll()).thenReturn(List.of(dummyQuestion));

        List<Question> result = quizService.getAllQuestions();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Question", result.get(0).getText());
    }

    @Test
    void getQuestionById_shouldReturnQuestion() {
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(dummyQuestion));

        Question result = quizService.getQuestionById(questionId);

        assertNotNull(result);
        assertEquals(questionId, result.getId());
    }

    @Test
    void getQuestionById_shouldThrowExceptionWhenNotFound() {
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizService.getQuestionById(questionId));
    }

    @Test
    void deleteQuestion_shouldCallRepositoryDelete() {
        doNothing().when(questionRepository).deleteById(questionId);

        quizService.deleteQuestion(questionId);

        verify(questionRepository, times(1)).deleteById(questionId);
    }

    @Test
    void getQuestionsByArticle_shouldReturnList() {
        when(questionRepository.findByArticleId(articleId)).thenReturn(List.of(dummyQuestion));

        List<Question> result = quizService.getQuestionsByArticle(articleId);

        assertFalse(result.isEmpty());
        assertEquals(articleId, result.get(0).getArticle().getId());
    }

    @Test
    void updateQuestion_shouldReturnUpdatedQuestion() {
        UpdateQuestionRequest request = new UpdateQuestionRequest();
        request.setText("Updated Text");
        request.setCorrectAnswer("False");
        
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(dummyQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(dummyQuestion);

        Question result = quizService.updateQuestion(questionId, request);

        assertNotNull(result);
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void updateQuestion_shouldThrowExceptionWhenNotFound() {
        UpdateQuestionRequest request = new UpdateQuestionRequest();
        request.setText("Updated Text");
        request.setCorrectAnswer("False");
        
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizService.updateQuestion(questionId, request));
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void setPassingScore_shouldUpdateAndSaveArticle() {
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(dummyArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(dummyArticle);

        quizService.setPassingScore(articleId, 80);

        assertEquals(80, dummyArticle.getPassingScore());
        verify(articleRepository, times(1)).save(dummyArticle);
    }

    @Test
    void setPassingScore_shouldThrowExceptionWhenArticleNotFound() {
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizService.setPassingScore(articleId, 80));
        verify(articleRepository, never()).save(any(Article.class));
    }
}
