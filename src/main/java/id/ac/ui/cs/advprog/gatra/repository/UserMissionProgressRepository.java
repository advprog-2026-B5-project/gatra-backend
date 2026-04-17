package id.ac.ui.cs.advprog.gatra.repository;

import id.ac.ui.cs.advprog.gatra.model.UserMissionProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserMissionProgressRepository extends JpaRepository<UserMissionProgress, UUID> {
    List<UserMissionProgress> findByUserId(UUID userId);
    Optional<UserMissionProgress> findByUserIdAndMissionId(UUID userId, UUID missionId);
}