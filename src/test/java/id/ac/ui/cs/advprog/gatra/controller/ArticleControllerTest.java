package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.dto.ArticleRequest;
import id.ac.ui.cs.advprog.gatra.dto.ArticleResponse;
import id.ac.ui.cs.advprog.gatra.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

    private static final String DUMMY_USERNAME = "admin";
    private static final String DUMMY_TITLE = "Test Article";
    private static final String DUMMY_CONTENT = "Test Content";
    private static final String DUMMY_CATEGORY_NAME = "Technology";

    @Mock private ArticleService articleService;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private ArticleController articleController;

    private UUID articleId;
    private UUID categoryId;
    private ArticleRequest request;
    private ArticleResponse response;

    @BeforeEach
    void setUp() {
        articleId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        request = new ArticleRequest();
        request.setTitle(DUMMY_TITLE);
        request.setContent(DUMMY_CONTENT);
        request.setCategoryId(categoryId);

        response = ArticleResponse.builder()
                .id(articleId)
                .title(DUMMY_TITLE)
                .content(DUMMY_CONTENT)
                .categoryId(categoryId)
                .categoryName(DUMMY_CATEGORY_NAME)
                .createdBy(DUMMY_USERNAME)
                .build();

    }


    @Test
    void getAllArticles_shouldReturnOkWithList() {
        when(articleService.getAllArticles()).thenReturn(List.of(response));

        ResponseEntity<List<ArticleResponse>> result = articleController.getAllArticles();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(articleService, times(1)).getAllArticles();
    }

    @Test
    void getAllArticles_whenEmpty_shouldReturnOkWithEmptyList() {
        when(articleService.getAllArticles()).thenReturn(List.of());

        ResponseEntity<List<ArticleResponse>> result = articleController.getAllArticles();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
    }


    @Test
    void getArticleById_whenFound_shouldReturnOk() {
        when(articleService.getArticleById(articleId)).thenReturn(response);

        ResponseEntity<ArticleResponse> result = articleController.getArticleById(articleId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(articleService, times(1)).getArticleById(articleId);
    }

    @Test
    void createArticle_whenValid_shouldReturnOk() {
        when(userDetails.getUsername()).thenReturn(DUMMY_USERNAME); // ← pindah ke sini
        when(articleService.createArticle(any(ArticleRequest.class), eq(DUMMY_USERNAME)))
                .thenReturn(response);

        ResponseEntity<ArticleResponse> result = articleController.createArticle(request, userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(articleService, times(1)).createArticle(any(ArticleRequest.class), eq(DUMMY_USERNAME));
    }


    @Test
    void updateArticle_whenValid_shouldReturnOk() {
        when(articleService.updateArticle(eq(articleId), any(ArticleRequest.class)))
                .thenReturn(response);

        ResponseEntity<ArticleResponse> result = articleController.updateArticle(articleId, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(articleService, times(1)).updateArticle(eq(articleId), any(ArticleRequest.class));
    }


    @Test
    void deleteArticle_whenFound_shouldReturnNoContent() {
        doNothing().when(articleService).deleteArticle(articleId);

        ResponseEntity<Void> result = articleController.deleteArticle(articleId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(articleService, times(1)).deleteArticle(articleId);
    }
}
