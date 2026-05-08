package id.ac.ui.cs.advprog.gatra.achievement.repository;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    List<UserAchievement> findByUserUsername(String username);
    List<UserAchievement> findByUserUsernameAndIsDisplayedTrue(String username);
    Optional<UserAchievement> findByUserUsernameAndAchievementId(String username, UUID achievementId);
    long countByUserUsernameAndIsDisplayedTrue(String username);
    boolean existsByUserIdAndAchievementId(UUID userId, UUID achievementId);
}