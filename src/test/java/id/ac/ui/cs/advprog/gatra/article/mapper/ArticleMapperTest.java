package id.ac.ui.cs.advprog.gatra.article.mapper;

import id.ac.ui.cs.advprog.gatra.article.dto.ArticleResponse;
import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.article.model.Category;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ArticleMapperTest {

    private ArticleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ArticleMapper();
    }

    @Test
    void toResponse_success() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Tech");

        User user = new User();
        user.setUsername("admin");

        UUID articleId = UUID.randomUUID();
        Article article = new Article();
        article.setId(articleId);
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setCategory(category);
        article.setCreatedBy(user);

        ArticleResponse response = mapper.toResponse(article);

        assertNotNull(response);
        assertEquals(articleId, response.getId());
        assertEquals("Test Article", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals(categoryId, response.getCategoryId());
        assertEquals("Tech", response.getCategoryName());
        assertEquals("admin", response.getCreatedBy());
    }
}