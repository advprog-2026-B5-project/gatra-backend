package id.ac.ui.cs.advprog.gatra.quiz.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.article.model.Category;
import id.ac.ui.cs.advprog.gatra.article.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.article.repository.CategoryRepository;
import id.ac.ui.cs.advprog.gatra.auth.model.Role;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.quiz.dto.SubmitQuizRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QuizFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("Functional: Complete Quiz Flow (Create, Fetch, Submit Attempt, and Check Status)")
    @WithMockUser(username = "gatra_student", roles = "STUDENT")
    void testCompleteQuizFlow() throws Exception {

        
        // 0. PERSIAPAN DATA (SEEDING)
        // 0a. Buat User (Student)
        User student = new User();
        student.setUsername("gatra_student");
        student.setEmail("student@gatra.com");
        student.setPassword("rahasia123");
        student.setRole(Role.ROLE_STUDENT);
        student = userRepository.save(student);
        UUID userId = student.getId();

        // 0b. Buat Kategori
        Category category = new Category();
        category.setName("Pemrograman Java");
        category = categoryRepository.save(category);

        // 0c. Buat Artikel untuk ditempelkan Kuis
        Article article = new Article();
        article.setTitle("Mengenal Spring Boot");
        article.setContent("Spring Boot adalah framework Java.");
        article.setCategory(category);
        article.setCreatedBy(student);
        article.setPassingScore(80);

        article = articleRepository.save(article);
        UUID articleId = article.getId();

        // 1. CREATE QUESTION (POST)
        java.util.Map<String, Object> createReq = new java.util.HashMap<>();
        createReq.put("type", "MULTIPLE_CHOICE");
        createReq.put("text", "Apa itu Spring Boot?");
        createReq.put("options", Arrays.asList("Framework Java", "Sistem Operasi", "Bahasa Pemrograman", "Browser"));
        createReq.put("correctAnswer", "Framework Java");
        createReq.put("articleId", articleId.toString());

        MvcResult questionResult = mockMvc.perform(post("/api/quiz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Apa itu Spring Boot?"))
                .andReturn();

        String questionResponseStr = questionResult.getResponse().getContentAsString();
        String questionIdStr = JsonPath.parse(questionResponseStr).read("$.id");
        UUID questionId = UUID.fromString(questionIdStr);

        
        // 2. GET QUESTIONS BY ARTICLE (GET)
        mockMvc.perform(get("/api/quiz/article/" + articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(questionId.toString()));

        
        // 3. SUBMIT QUIZ ATTEMPT (POST)
        SubmitQuizRequest submitRequest = new SubmitQuizRequest();
        submitRequest.setArticleId(articleId);
        submitRequest.setUserId(userId);

        SubmitQuizRequest.AnswerItem answerItem = new SubmitQuizRequest.AnswerItem();
        answerItem.setQuestionId(questionId);
        answerItem.setAnswer("Framework Java"); // Mengirimkan jawaban yang benar

        submitRequest.setAnswers(List.of(answerItem));

        mockMvc.perform(post("/api/quiz/attempt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passed").value(true)) // Berharap passed bernilai true
                .andExpect(jsonPath("$.score").isNumber()); // Cek apakah score dihitung

        
        // 4. CHECK ATTEMPT STATUS (GET)
        mockMvc.perform(get("/api/quiz/attempt/status")
                        .param("userId", userId.toString())
                        .param("articleId", articleId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true")); // Endpoint ini mengembalikan nilai boolean secara langsung
    }
}