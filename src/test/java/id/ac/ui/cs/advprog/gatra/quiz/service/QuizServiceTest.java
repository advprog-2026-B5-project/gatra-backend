package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.dto.UpdateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.MultipleChoiceQuestion;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuestionRepository;
import id.ac.ui.cs.advprog.gatra.article.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private QuestionFactory questionFactory;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    void shouldCreateQuestionSuccessfully() {
        Article mockArticle = new Article();

        CreateQuestionRequest request = new CreateQuestionRequest(
                "Ibukota Indonesia?",
                List.of("Jakarta", "Bandung"),
                "Jakarta",
                UUID.randomUUID()
        );

        Question mockQuestion = mock(Question.class);

        when(articleRepository.findById(any())).thenReturn(Optional.of(mockArticle));

        when(questionFactory.create(any())).thenReturn(mockQuestion);

        when(questionRepository.save(any())).thenReturn(mockQuestion);

        Question result = quizService.createQuestion(request);

        assertNotNull(result);
        verify(questionRepository, times(1)).save(any());

    }

    @Test
    void shouldGetQuestionByIdSuccessfully() {

        UUID id = UUID.randomUUID();

        Question mockQuestion = mock(Question.class);

        when(questionRepository.findById(id)).thenReturn(java.util.Optional.of(mockQuestion));

        Question result = quizService.getQuestionById(id);

        assertNotNull(result);
        assertEquals(mockQuestion, result);

        verify(questionRepository, times(1)).findById(id);
    }

    @Test
    void shouldDeleteQuestionSuccessfully() {

        UUID id = UUID.randomUUID();

        doNothing().when(questionRepository).deleteById(id);

        quizService.deleteQuestion(id);

        verify(questionRepository, times(1)).deleteById(id);
    }


    @Test
    void shouldUpdateQuestionSuccessfully() {
        UUID id = UUID.randomUUID();

        MultipleChoiceQuestion existing = new MultipleChoiceQuestion();
        existing.setText("Lama");
        existing.setOptions(List.of("A", "B"));
        existing.setCorrectAnswer("A");

        UpdateQuestionRequest request = new UpdateQuestionRequest();
        request.setText("Baru");
        request.setOptions(List.of("C", "D"));
        request.setCorrectAnswer("C");

        when(questionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(questionRepository.save(any())).thenReturn(existing);

        Question result = quizService.updateQuestion(id, request);

        assertNotNull(result);
        assertEquals("Baru", result.getText());
        verify(questionRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenUpdateQuestionNotFound() {
        UUID id = UUID.randomUUID();
        UpdateQuestionRequest request = new UpdateQuestionRequest();

        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizService.updateQuestion(id, request));
    }

    @Test
    void shouldSetPassingScoreSuccessfully() {
        UUID articleId = UUID.randomUUID();

        Article article = new Article();
        article.setPassingScore(0);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        quizService.setPassingScore(articleId, 70);

        assertEquals(70, article.getPassingScore());
        verify(articleRepository, times(1)).save(article);
    }

    @Test
    void shouldThrowWhenArticleNotFoundForPassingScore() {
        UUID articleId = UUID.randomUUID();

        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizService.setPassingScore(articleId, 70));
    }
}