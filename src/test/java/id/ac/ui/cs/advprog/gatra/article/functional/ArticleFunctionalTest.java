package id.ac.ui.cs.advprog.gatra.article.functional;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.article.controller.ArticleController;
import id.ac.ui.cs.advprog.gatra.article.dto.ArticleResponse;
import id.ac.ui.cs.advprog.gatra.article.service.ArticleService;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ArticleFunctionalTest {

    private MockMvc mockMvc;

    @Mock
    private ArticleService articleService;

    @Mock
    private UserRepository userRepository;

    private final UserDetails mockUserDetails = User
            .withUsername("admin")
            .password("password")
            .roles("ADMIN")
            .build();

    @BeforeEach
    void setUp() {
        ArticleController articleController = new ArticleController(articleService, userRepository);

        mockMvc = MockMvcBuilders
                .standaloneSetup(articleController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                                && UserDetails.class.isAssignableFrom(parameter.getParameterType());
                    }

                    @Override
                    public Object resolveArgument(
                            MethodParameter parameter,
                            ModelAndViewContainer mavContainer,
                            NativeWebRequest webRequest,
                            org.springframework.web.bind.support.WebDataBinderFactory binderFactory
                    ) {
                        return mockUserDetails;
                    }
                })
                .build();
    }

    @Test
    @DisplayName("GET /api/articles should return 200 OK")
    void getAllArticlesShouldReturnOk() throws Exception {
        when(articleService.getAllArticles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk());

        verify(articleService).getAllArticles();
    }

    @Test
    @DisplayName("GET /api/articles/{id} should return 200 OK")
    void getArticleByIdShouldReturnOk() throws Exception {
        UUID articleId = UUID.randomUUID();
        ArticleResponse response = null;

        when(articleService.getArticleById(articleId)).thenReturn(response);

        mockMvc.perform(get("/api/articles/{id}", articleId))
                .andExpect(status().isOk());

        verify(articleService).getArticleById(articleId);
    }

    @Test
    @DisplayName("POST /api/articles should create article using authenticated username")
    void createArticleShouldReturnOk() throws Exception {
        UUID categoryId = UUID.randomUUID();

        String requestBody = """
                {
                  "title": "Artikel Functional Test",
                  "content": "Konten artikel untuk functional test",
                  "categoryId": "%s"
                }
                """.formatted(categoryId);

        when(articleService.createArticle(any(), eq("admin"))).thenReturn(null);

        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(articleService).createArticle(any(), eq("admin"));
    }

    @Test
    @DisplayName("PUT /api/articles/{id} should update article")
    void updateArticleShouldReturnOk() throws Exception {
        UUID articleId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        String requestBody = """
                {
                  "title": "Artikel Updated",
                  "content": "Konten artikel updated",
                  "categoryId": "%s"
                }
                """.formatted(categoryId);

        when(articleService.updateArticle(eq(articleId), any())).thenReturn(null);

        mockMvc.perform(put("/api/articles/{id}", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(articleService).updateArticle(eq(articleId), any());
    }

    @Test
    @DisplayName("DELETE /api/articles/{id} should delete article and return 204")
    void deleteArticleShouldReturnNoContent() throws Exception {
        UUID articleId = UUID.randomUUID();

        mockMvc.perform(delete("/api/articles/{id}", articleId))
                .andExpect(status().isNoContent());

        verify(articleService).deleteArticle(articleId, "admin");
    }

    @Test
    @DisplayName("POST /api/articles/{id}/read should mark article as read")
    void markArticleAsReadShouldReturnOk() throws Exception {
        UUID articleId = UUID.randomUUID();
        MilestoneResponse response = null;

        when(articleService.markAsRead(articleId, "admin")).thenReturn(response);

        mockMvc.perform(post("/api/articles/{id}/read", articleId))
                .andExpect(status().isOk());

        verify(articleService).markAsRead(articleId, "admin");
    }

    @Test
    @DisplayName("GET /api/articles/deleted should return deleted articles")
    void getDeletedArticlesShouldReturnOk() throws Exception {
        when(articleService.getDeletedArticles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/articles/deleted"))
                .andExpect(status().isOk());

        verify(articleService).getDeletedArticles();
    }
}