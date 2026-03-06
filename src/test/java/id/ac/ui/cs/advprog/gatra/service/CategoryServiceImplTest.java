package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.CategoryRequest;
import id.ac.ui.cs.advprog.gatra.dto.CategoryResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.mapper.CategoryMapper;
import id.ac.ui.cs.advprog.gatra.model.Category;
import id.ac.ui.cs.advprog.gatra.repository.CategoryRepository;
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
class CategoryServiceImplTest {

    private static final String DUMMY_NAME = "Technology";
    private static final String UPDATED_NAME = "Science";

    @Mock private CategoryRepository categoryRepository;
    @Mock private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UUID categoryId;
    private Category category;
    private CategoryRequest request;
    private CategoryResponse response;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        category = Category.builder()
                .id(categoryId)
                .name(DUMMY_NAME)
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
    void deleteCategory_whenFound_shouldDelete() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId));

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void deleteCategory_whenNotFound_shouldThrowException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId));

        verify(categoryRepository, never()).deleteById(any());
    }
}
