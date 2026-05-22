package id.ac.ui.cs.advprog.gatra.article.mapper;

import id.ac.ui.cs.advprog.gatra.article.dto.CategoryResponse;
import id.ac.ui.cs.advprog.gatra.article.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    private CategoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoryMapper();
    }

    @Test
    void toResponse_success() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Tech");

        CategoryResponse response = mapper.toResponse(category);

        assertNotNull(response);
        assertEquals(categoryId, response.getId());
        assertEquals("Tech", response.getName());
    }
}