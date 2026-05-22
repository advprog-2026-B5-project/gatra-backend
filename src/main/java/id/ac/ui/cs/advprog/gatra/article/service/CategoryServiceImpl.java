package id.ac.ui.cs.advprog.gatra.article.service;

import id.ac.ui.cs.advprog.gatra.article.dto.CategoryRequest;
import id.ac.ui.cs.advprog.gatra.article.dto.CategoryResponse;
import id.ac.ui.cs.advprog.gatra.article.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.article.mapper.CategoryMapper;
import id.ac.ui.cs.advprog.gatra.article.model.Category;
import id.ac.ui.cs.advprog.gatra.article.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private static final String fallback_category_name = "Dll";
    private final ArticleRepository articleRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        return categoryMapper.toResponse(findCategoryOrThrow(id));
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .build();
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = findCategoryOrThrow(id);
        category.setName(request.getName());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    private Category findCategoryOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = findCategoryOrThrow(id);
        reassignArticlesToFallback(id);
        softDeleteCategory(category);
    }

    private void reassignArticlesToFallback(UUID categoryId) {
        Category fallback = getOrCreateFallbackCategory();
        articleRepository.findByCategoryId(categoryId)
                .forEach(article -> {
                    article.setCategory(fallback);
                    articleRepository.save(article);
                });
    }

    private Category getOrCreateFallbackCategory() {
        return categoryRepository.findByName(fallback_category_name)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder().name(fallback_category_name).build()));
    }

    private void softDeleteCategory(Category category) {
        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }
}