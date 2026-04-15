package id.ac.ui.cs.advprog.gatra.repository;

import id.ac.ui.cs.advprog.gatra.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    @Query(value = "SELECT * FROM articles WHERE id = :id", nativeQuery = true)
    Optional<Article> findByIdIncludeDeleted(@Param("id") UUID id);

    @Query(value = "SELECT * FROM articles WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC", nativeQuery = true)
    List<Article> findAllDeleted();
}