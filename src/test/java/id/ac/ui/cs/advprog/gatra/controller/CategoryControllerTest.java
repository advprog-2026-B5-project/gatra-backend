package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.dto.CategoryRequest;
import id.ac.ui.cs.advprog.gatra.dto.CategoryResponse;
import id.ac.ui.cs.advprog.gatra.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private static final String DUMMY_NAME = "Technology";
    private static final String UPDATED_NAME = "Science";

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private UUID categoryId;
    private CategoryRequest request;
    private CategoryResponse response;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        request = new CategoryRequest();
        request.setName(DUMMY_NAME);

        response = CategoryResponse.builder()
                .id(categoryId)
                .name(DUMMY_NAME)
                .build();
    }

    @Test
    void getAllCategories_shouldReturnOkWithList() {
        when(categoryService.getAllCategories()).thenReturn(List.of(response));

        ResponseEntity<List<CategoryResponse>> result = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void getAllCategories_whenEmpty_shouldReturnOkWithEmptyList() {
        when(categoryService.getAllCategories()).thenReturn(List.of());

        ResponseEntity<List<CategoryResponse>> result = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void getCategoryById_whenFound_shouldReturnOk() {
        when(categoryService.getCategoryById(categoryId)).thenReturn(response);

        ResponseEntity<CategoryResponse> result = categoryController.getCategoryById(categoryId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(categoryService, times(1)).getCategoryById(categoryId);
    }

    @Test
    void createCategory_whenValid_shouldReturnOk() {
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(response);

        ResponseEntity<CategoryResponse> result = categoryController.createCategory(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(categoryService, times(1)).createCategory(any(CategoryRequest.class));
    }

    @Test
    void updateCategory_whenValid_shouldReturnOk() {
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName(UPDATED_NAME);

        CategoryResponse updatedResponse = CategoryResponse.builder()
                .id(categoryId)
                .name(UPDATED_NAME)
                .build();

        when(categoryService.updateCategory(eq(categoryId), any(CategoryRequest.class)))
                .thenReturn(updatedResponse);

        ResponseEntity<CategoryResponse> result = categoryController.updateCategory(categoryId, updateRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(UPDATED_NAME, result.getBody().getName());
        verify(categoryService, times(1)).updateCategory(eq(categoryId), any(CategoryRequest.class));
    }

    @Test
    void deleteCategory_whenFound_shouldReturnNoContent() {
        doNothing().when(categoryService).deleteCategory(categoryId);

        ResponseEntity<Void> result = categoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(categoryService, times(1)).deleteCategory(categoryId);
    }
}