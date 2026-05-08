package id.ac.ui.cs.advprog.gatra.achievement.repository;

import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    boolean existsByName(String name);
    List<Achievement> findByCategoryAndMilestoneThresholdLessThanEqual(ActionType category, Integer threshold);
    Optional<Achievement> findByName(String name);
}