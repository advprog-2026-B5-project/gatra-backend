package id.ac.ui.cs.advprog.gatra.repository;

import id.ac.ui.cs.advprog.gatra.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    boolean existsByName(String name);
}