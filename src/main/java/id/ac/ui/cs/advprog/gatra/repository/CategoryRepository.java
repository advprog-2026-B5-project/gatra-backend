package id.ac.ui.cs.advprog.gatra.repository;

import id.ac.ui.cs.advprog.gatra.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}