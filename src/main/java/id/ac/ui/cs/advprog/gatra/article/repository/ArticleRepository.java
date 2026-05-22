package id.ac.ui.cs.advprog.gatra.article.repository;

import id.ac.ui.cs.advprog.gatra.article.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    List<Article> findByCategoryId(UUID categoryId);
}