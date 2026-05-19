package id.ac.ui.cs.advprog.gatra.article.repository;

import id.ac.ui.cs.advprog.gatra.article.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}