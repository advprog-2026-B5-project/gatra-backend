package id.ac.ui.cs.advprog.gatra.achievement.repository;

import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.model.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    boolean existsByName(String name);
    List<Achievement> findByCategoryAndMilestoneThresholdLessThanEqual(ActionType category, Integer threshold);
}