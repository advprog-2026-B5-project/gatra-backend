package id.ac.ui.cs.advprog.gatra.achievement.repository;

import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.model.StudentMilestoneProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentMilestoneProgressRepository extends JpaRepository<StudentMilestoneProgress, UUID> {
    Optional<StudentMilestoneProgress> findByUserIdAndActionType(UUID userId, ActionType actionType);
    void deleteByUserId(UUID userId);
}
