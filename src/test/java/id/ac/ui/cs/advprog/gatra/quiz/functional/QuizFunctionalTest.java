package id.ac.ui.cs.advprog.gatra.quiz.functional;

import id.ac.ui.cs.advprog.gatra.quiz.controller.QuizController;
import id.ac.ui.cs.advprog.gatra.quiz.mapper.QuestionMapper;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.service.QuizAttemptService;
import id.ac.ui.cs.advprog.gatra.quiz.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class QuizFunctionalTest {

    private MockMvc mockMvc;

    @Mock
    private QuizService quizService;

    @Mock
    private QuizAttemptService quizAttemptService;

    @Mock
    private QuestionMapper questionMapper;

    @BeforeEach
    void setUp() {
        QuizController quizController = new QuizController(
                quizService,
                quizAttemptService,
                questionMapper
        );

        mockMvc = MockMvcBuilders.standaloneSetup(quizController).build();
    }

    @Test
    @DisplayName("POST /api/quiz should create question and return 201")
    void createQuestionShouldReturnCreated() throws Exception {
        UUID articleId = UUID.randomUUID();
        Question question = mock(Question.class);

        String requestBody = """
                {
                  "articleId": "%s",
                  "type": "MULTIPLE_CHOICE",
                  "text": "Apa jawaban yang benar?",
                  "options": ["A", "B", "C", "D"],
                  "correctAnswer": "A"
                }
                """.formatted(articleId);

        when(quizService.createQuestion(any())).thenReturn(question);
        when(questionMapper.toResponse(question)).thenReturn(null);

        mockMvc.perform(post("/api/quiz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(quizService).createQuestion(any());
        verify(questionMapper).toResponse(question);
    }

    @Test
    @DisplayName("GET /api/quiz should return 200 OK")
    void getAllQuestionsShouldReturnOk() throws Exception {
        when(quizService.getAllQuestions()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/quiz"))
                .andExpect(status().isOk());

        verify(quizService).getAllQuestions();
    }

    @Test
    @DisplayName("GET /api/quiz/{id} should return question by id")
    void getQuestionByIdShouldReturnOk() throws Exception {
        UUID questionId = UUID.randomUUID();
        Question question = mock(Question.class);

        when(quizService.getQuestionById(questionId)).thenReturn(question);
        when(questionMapper.toResponse(question)).thenReturn(null);

        mockMvc.perform(get("/api/quiz/{id}", questionId))
                .andExpect(status().isOk());

        verify(quizService).getQuestionById(questionId);
        verify(questionMapper).toResponse(question);
    }

    @Test
    @DisplayName("GET /api/quiz/article/{articleId} should return questions by article")
    void getQuestionsByArticleShouldReturnOk() throws Exception {
        UUID articleId = UUID.randomUUID();

        when(quizService.getQuestionsByArticle(articleId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/quiz/article/{articleId}", articleId))
                .andExpect(status().isOk());

        verify(quizService).getQuestionsByArticle(articleId);
    }

    @Test
    @DisplayName("PUT /api/quiz/{id} should update question")
    void updateQuestionShouldReturnOk() throws Exception {
        UUID questionId = UUID.randomUUID();
        Question question = mock(Question.class);

        String requestBody = """
                {
                  "text": "Pertanyaan setelah update",
                  "options": ["A", "B", "C", "D"],
                  "correctAnswer": "B"
                }
                """;

        when(quizService.updateQuestion(eq(questionId), any())).thenReturn(question);
        when(questionMapper.toResponse(question)).thenReturn(null);

        mockMvc.perform(put("/api/quiz/{id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(quizService).updateQuestion(eq(questionId), any());
        verify(questionMapper).toResponse(question);
    }

    @Test
    @DisplayName("DELETE /api/quiz/{id} should delete question and return 204")
    void deleteQuestionShouldReturnNoContent() throws Exception {
        UUID questionId = UUID.randomUUID();

        mockMvc.perform(delete("/api/quiz/{id}", questionId))
                .andExpect(status().isNoContent());

        verify(quizService).deleteQuestion(questionId);
    }

    @Test
    @DisplayName("PATCH /api/quiz/passing-score/{articleId} should update passing score")
    void setPassingScoreShouldReturnNoContent() throws Exception {
        UUID articleId = UUID.randomUUID();

        String requestBody = """
                {
                  "passingScore": 70
                }
                """;

        mockMvc.perform(patch("/api/quiz/passing-score/{articleId}", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        verify(quizService).setPassingScore(articleId, 70);
    }

    @Test
    @DisplayName("POST /api/quiz/attempt should submit quiz")
    void submitQuizShouldReturnOk() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        String requestBody = """
                {
                  "userId": "%s",
                  "articleId": "%s",
                  "answers": [
                    {
                      "questionId": "%s",
                      "answer": "A"
                    }
                  ]
                }
                """.formatted(userId, articleId, questionId);

        when(quizAttemptService.submitQuiz(any())).thenReturn(null);

        mockMvc.perform(post("/api/quiz/attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(quizAttemptService).submitQuiz(any());
    }

    @Test
    @DisplayName("GET /api/quiz/attempt/status should return quiz attempt status")
    void checkStatusShouldReturnOk() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();

        when(quizAttemptService.hasUserPassed(userId, articleId)).thenReturn(true);

        mockMvc.perform(get("/api/quiz/attempt/status")
                        .param("userId", userId.toString())
                        .param("articleId", articleId.toString()))
                .andExpect(status().isOk());

        verify(quizAttemptService).hasUserPassed(userId, articleId);
    }
}
