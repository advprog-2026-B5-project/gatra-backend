package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.ArticleRequest;
import id.ac.ui.cs.advprog.gatra.dto.ArticleResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.mapper.ArticleMapper;
import id.ac.ui.cs.advprog.gatra.model.Article;
import id.ac.ui.cs.advprog.gatra.model.Category;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.repository.CategoryRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
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
class ArticleServiceImplTest {

    private static final String DUMMY_USERNAME = "admin";
    private static final String DUMMY_TITLE = "Test Article";
    private static final String DUMMY_CONTENT = "Test Content";
    private static final String DUMMY_CATEGORY_NAME = "Technology";

    @Mock private ArticleRepository articleRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private UUID articleId;
    private UUID categoryId;
    private Article article;
    private Category category;
    private User user;
    private ArticleRequest request;
    private ArticleResponse response;

    @BeforeEach
    void setUp() {
        articleId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = Category.builder()
                .id(categoryId)
                .name(DUMMY_CATEGORY_NAME)
                .build();

        user = User.builder()
                .username(DUMMY_USERNAME)
                .build();

        article = Article.builder()
                .id(articleId)
                .title(DUMMY_TITLE)
                .content(DUMMY_CONTENT)
                .category(category)
                .createdBy(user)
                .build();

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
    void getAllArticles_shouldReturnListOfArticles() {
        when(articleRepository.findAll()).thenReturn(List.of(article));
        when(articleMapper.toResponse(article)).thenReturn(response);

        List<ArticleResponse> result = articleService.getAllArticles();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
        verify(articleRepository, times(1)).findAll();
    }

    @Test
    void getAllArticles_whenEmpty_shouldReturnEmptyList() {
        when(articleRepository.findAll()).thenReturn(List.of());

        List<ArticleResponse> result = articleService.getAllArticles();

        assertTrue(result.isEmpty());
    }


    @Test
    void getArticleById_whenFound_shouldReturnArticle() {
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleMapper.toResponse(article)).thenReturn(response);

        ArticleResponse result = articleService.getArticleById(articleId);

        assertEquals(response, result);
        verify(articleRepository, times(1)).findById(articleId);
    }

    @Test
    void getArticleById_whenNotFound_shouldThrowException() {
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> articleService.getArticleById(articleId));

        verify(articleMapper, never()).toResponse(any());
    }


    @Test
    void createArticle_whenValid_shouldReturnCreatedArticle() {
        when(userRepository.findByUsername(DUMMY_USERNAME)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(articleRepository.save(any(Article.class))).thenReturn(article);
        when(articleMapper.toResponse(article)).thenReturn(response);

        ArticleResponse result = articleService.createArticle(request, DUMMY_USERNAME);

        assertEquals(response, result);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void createArticle_whenUserNotFound_shouldThrowException() {
        when(userRepository.findByUsername(DUMMY_USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> articleService.createArticle(request, DUMMY_USERNAME));

        verify(articleRepository, never()).save(any());
    }

    @Test
    void createArticle_whenCategoryNotFound_shouldThrowException() {
        when(userRepository.findByUsername(DUMMY_USERNAME)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> articleService.createArticle(request, DUMMY_USERNAME));

        verify(articleRepository, never()).save(any());
    }


    @Test
    void updateArticle_whenValid_shouldReturnUpdatedArticle() {
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(articleRepository.save(any(Article.class))).thenReturn(article);
        when(articleMapper.toResponse(article)).thenReturn(response);

        ArticleResponse result = articleService.updateArticle(articleId, request);

        assertEquals(response, result);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void updateArticle_whenArticleNotFound_shouldThrowException() {
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> articleService.updateArticle(articleId, request));

        verify(articleRepository, never()).save(any());
    }

    @Test
    void updateArticle_whenCategoryNotFound_shouldThrowException() {
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> articleService.updateArticle(articleId, request));

        verify(articleRepository, never()).save(any());
    }


    @Test
    void deleteArticle_whenFound_shouldDelete() {
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        assertDoesNotThrow(() -> articleService.deleteArticle(articleId));

        verify(articleRepository, times(1)).deleteById(articleId);
    }

    @Test
    void deleteArticle_whenNotFound_shouldThrowException() {
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> articleService.deleteArticle(articleId));

        verify(articleRepository, never()).deleteById(any());
    }
}