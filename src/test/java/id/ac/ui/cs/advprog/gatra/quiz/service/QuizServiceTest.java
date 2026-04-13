package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.quiz.dto.CreateQuestionRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    void shouldCreateQuestionSuccessfully() {

        CreateQuestionRequest request = new CreateQuestionRequest(
                "Ibukota Indonesia?",
                List.of("Jakarta", "Bandung"),
                "Jakarta",
                UUID.randomUUID()
        );

        Question savedQuestion = mock(Question.class);

        when(questionRepository.save(any())).thenReturn(savedQuestion);

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
}