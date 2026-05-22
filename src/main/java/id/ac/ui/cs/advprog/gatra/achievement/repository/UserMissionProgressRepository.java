package id.ac.ui.cs.advprog.gatra.achievement.repository;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserMissionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMissionProgressRepository extends JpaRepository<UserMissionProgress, UUID> {
    List<UserMissionProgress> findByUserId(UUID userId);
    Optional<UserMissionProgress> findByUserIdAndMissionId(UUID userId, UUID missionId);
    void deleteByUserId(UUID userId);
    long countByIsClaimedTrue();
}