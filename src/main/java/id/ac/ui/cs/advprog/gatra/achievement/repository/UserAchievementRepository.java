package id.ac.ui.cs.advprog.gatra.achievement.repository;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.userId = :userId")
    List<UserAchievement> findByUserId(UUID userId);
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.userId = :userId AND ua.isDisplayed = true")
    List<UserAchievement> findByUserIdAndIsDisplayedTrue(UUID userId);
    Optional<UserAchievement> findByUserIdAndAchievementId(UUID userId, UUID achievementId);
    long countByUserIdAndIsDisplayedTrue(UUID userId);
    boolean existsByUserIdAndAchievementId(UUID userId, UUID achievementId);
    void deleteByUserId(UUID userId);
}