package id.ac.ui.cs.advprog.gatra.article.mapper;

import id.ac.ui.cs.advprog.gatra.article.dto.CategoryResponse;
import id.ac.ui.cs.advprog.gatra.article.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}