package id.ac.ui.cs.advprog.gatra.quiz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.auth.security.JwtUtil;
import id.ac.ui.cs.advprog.gatra.quiz.dto.*;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.model.TrueFalseQuestion;
import id.ac.ui.cs.advprog.gatra.quiz.service.QuizAttemptService;
import id.ac.ui.cs.advprog.gatra.quiz.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = QuizController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuizService quizService;

    @MockitoBean
    private QuizAttemptService quizAttemptService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private TrueFalseQuestion dummyQuestion;
    private UUID dummyId;
    private UUID articleId;

    @BeforeEach
    void setUp() {
        dummyId = UUID.randomUUID();
        articleId = UUID.randomUUID();
        
        dummyQuestion = new TrueFalseQuestion();
        dummyQuestion.setId(dummyId);
        dummyQuestion.setText("Test Question");
        dummyQuestion.setCorrectAnswer("True");
    }

    @Test
    @WithMockUser
    void createQuestion_shouldReturnOkAndQuestion() throws Exception {
        CreateQuestionRequest request = new CreateQuestionRequest("Test Question", null, "True", articleId);
        
        when(quizService.createQuestion(any(CreateQuestionRequest.class))).thenReturn(dummyQuestion);

        mockMvc.perform(post("/api/quiz")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Test Question"));
    }

    @Test
    @WithMockUser
    void getAllQuestions_shouldReturnOkAndList() throws Exception {
        when(quizService.getAllQuestions()).thenReturn(List.of((Question) dummyQuestion));

        mockMvc.perform(get("/api/quiz")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Test Question"));
    }

    @Test
    @WithMockUser
    void getQuestionById_shouldReturnOkAndQuestion() throws Exception {
        when(quizService.getQuestionById(dummyId)).thenReturn(dummyQuestion);

        mockMvc.perform(get("/api/quiz/{id}", dummyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Test Question"));
    }

    @Test
    @WithMockUser
    void getQuestionsByArticle_shouldReturnOkAndList() throws Exception {
        when(quizService.getQuestionsByArticle(articleId)).thenReturn(List.of((Question) dummyQuestion));

        mockMvc.perform(get("/api/quiz/article/{articleId}", articleId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Test Question"));
    }

    @Test
    @WithMockUser
    void deleteQuestion_shouldReturnNoContent() throws Exception {
        doNothing().when(quizService).deleteQuestion(dummyId);

        mockMvc.perform(delete("/api/quiz/{id}", dummyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void updateQuestion_shouldReturnOkAndQuestion() throws Exception {
        String jsonRequest = "{\"text\":\"Updated Question\",\"correctAnswer\":\"False\"}";

        dummyQuestion.setText("Updated Question");
        when(quizService.updateQuestion(eq(dummyId), any(UpdateQuestionRequest.class))).thenReturn(dummyQuestion);

        mockMvc.perform(put("/api/quiz/{id}", dummyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated Question"));
    }

    @Test
    @WithMockUser
    void setPassingScore_shouldReturnNoContent() throws Exception {
        String jsonRequest = "{\"passingScore\":80}";
        
        doNothing().when(quizService).setPassingScore(eq(articleId), any(Integer.class));

        mockMvc.perform(patch("/api/quiz/passing-score/{articleId}", articleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void submitQuiz_shouldReturnOkAndResult() throws Exception {
        String jsonRequest = "{\"articleId\":\"" + articleId.toString() + "\",\"userId\":\"" + UUID.randomUUID().toString() + "\",\"answers\":[]}";
        QuizResultResponse response = new QuizResultResponse(100.0f, 80.0f, true, new ArrayList<>());
        
        when(quizAttemptService.submitQuiz(any(SubmitQuizRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/quiz/attempt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(100.0))
                .andExpect(jsonPath("$.passed").value(true));
    }

    @Test
    @WithMockUser
    void checkStatus_shouldReturnOkAndBoolean() throws Exception {
        UUID userId = UUID.randomUUID();
        when(quizAttemptService.hasUserPassed(userId, articleId)).thenReturn(true);

        mockMvc.perform(get("/api/quiz/attempt/status")
                .param("userId", userId.toString())
                .param("articleId", articleId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}