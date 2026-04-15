package id.ac.ui.cs.advprog.gatra.repository;

import id.ac.ui.cs.advprog.gatra.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    List<UserAchievement> findByUserUsername(String username);
    List<UserAchievement> findByUserUsernameAndIsDisplayedTrue(String username);
    Optional<UserAchievement> findByUserUsernameAndAchievementId(String username, UUID achievementId);
    long countShownAchievements(String username);
}