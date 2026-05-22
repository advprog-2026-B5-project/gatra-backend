package id.ac.ui.cs.advprog.gatra.article.service;

import id.ac.ui.cs.advprog.gatra.article.dto.CategoryRequest;
import id.ac.ui.cs.advprog.gatra.article.dto.CategoryResponse;
import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.article.model.Category;
import id.ac.ui.cs.advprog.gatra.article.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.article.repository.CategoryRepository;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.article.mapper.CategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    private static final String DUMMY_NAME = "Technology";
    private static final String UPDATED_NAME = "Science";
    private static final String FALLBACK_NAME = "Dll";

    @Mock private CategoryRepository categoryRepository;
    @Mock private ArticleRepository articleRepository;
    @Mock private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UUID categoryId;
    private Category category;
    private Category fallbackCategory;
    private CategoryRequest request;
    private CategoryResponse response;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        category = Category.builder()
                .id(categoryId)
                .name(DUMMY_NAME)
                .build();

        fallbackCategory = Category.builder()
                .id(UUID.randomUUID())
                .name(FALLBACK_NAME)
                .build();

        request = new CategoryRequest();
        request.setName(DUMMY_NAME);

        response = CategoryResponse.builder()
                .id(categoryId)
                .name(DUMMY_NAME)
                .build();
    }

    @Test
    void getAllCategories_shouldReturnListOfCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getAllCategories_whenEmpty_shouldReturnEmptyList() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertTrue(result.isEmpty());
        verify(categoryMapper, never()).toResponse(any());
    }

    @Test
    void getAllCategories_shouldFilterOutDeletedCategories() {
        Category deletedCategory = Category.builder()
                .id(UUID.randomUUID())
                .name("Deleted")
                .deletedAt(LocalDateTime.now())
                .build();

        when(categoryRepository.findAll()).thenReturn(List.of(category, deletedCategory));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        verify(categoryMapper, never()).toResponse(deletedCategory);
    }

    @Test
    void getCategoryById_whenFound_shouldReturnCategory() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        CategoryResponse result = categoryService.getCategoryById(categoryId);

        assertEquals(response, result);
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void getCategoryById_whenNotFound_shouldThrowException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(categoryId));

        verify(categoryMapper, never()).toResponse(any());
    }

    @Test
    void createCategory_whenValid_shouldReturnCreatedCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(response);

        CategoryResponse result = categoryService.createCategory(request);

        assertEquals(response, result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_shouldSaveCategoryWithCorrectName() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(response);

        categoryService.createCategory(request);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals(DUMMY_NAME, captor.getValue().getName());
    }

    @Test
    void updateCategory_whenFound_shouldReturnUpdatedCategory() {
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName(UPDATED_NAME);

        CategoryResponse updatedResponse = CategoryResponse.builder()
                .id(categoryId)
                .name(UPDATED_NAME)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(updatedResponse);

        CategoryResponse result = categoryService.updateCategory(categoryId, updateRequest);

        assertEquals(UPDATED_NAME, result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_whenNotFound_shouldThrowException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, request));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_shouldUpdateNameCorrectly() {
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName(UPDATED_NAME);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(response);

        categoryService.updateCategory(categoryId, updateRequest);

        assertEquals(UPDATED_NAME, category.getName());
    }

    @Test
    void deleteCategory_whenFound_shouldSoftDelete() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(FALLBACK_NAME)).thenReturn(Optional.of(fallbackCategory));
        when(articleRepository.findByCategoryId(categoryId)).thenReturn(List.of());

        assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId));

        assertNotNull(category.getDeletedAt());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void deleteCategory_whenNotFound_shouldThrowException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId));

        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void deleteCategory_whenFallbackNotExist_shouldCreateFallback() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(FALLBACK_NAME)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(fallbackCategory);
        when(articleRepository.findByCategoryId(categoryId)).thenReturn(List.of());

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(2)).save(any(Category.class));
    }

    @Test
    void deleteCategory_shouldReassignArticlesToFallback() {
        Article article = new Article();
        article.setCategory(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(FALLBACK_NAME)).thenReturn(Optional.of(fallbackCategory));
        when(articleRepository.findByCategoryId(categoryId)).thenReturn(List.of(article));

        categoryService.deleteCategory(categoryId);

        assertEquals(fallbackCategory, article.getCategory());
        verify(articleRepository, times(1)).save(article);
    }

    @Test
    void deleteCategory_withMultipleArticles_shouldReassignAll() {
        Article article1 = new Article();
        article1.setCategory(category);
        Article article2 = new Article();
        article2.setCategory(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(FALLBACK_NAME)).thenReturn(Optional.of(fallbackCategory));
        when(articleRepository.findByCategoryId(categoryId)).thenReturn(List.of(article1, article2));

        categoryService.deleteCategory(categoryId);

        assertEquals(fallbackCategory, article1.getCategory());
        assertEquals(fallbackCategory, article2.getCategory());
        verify(articleRepository, times(2)).save(any(Article.class));
    }
}