package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.CategoryRequest;
import id.ac.ui.cs.advprog.gatra.dto.CategoryResponse;
import id.ac.ui.cs.advprog.gatra.mapper.CategoryMapper;
import id.ac.ui.cs.advprog.gatra.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return null; // TODO
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        return null; // TODO
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        return null; // TODO
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        return null; // TODO
    }

    @Override
    public void deleteCategory(UUID id) {
        // TODO
    }
}