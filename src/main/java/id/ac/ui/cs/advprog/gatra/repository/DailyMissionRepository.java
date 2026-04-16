package id.ac.ui.cs.advprog.gatra.repository;
import id.ac.ui.cs.advprog.gatra.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.model.MissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, UUID> {
    List<DailyMission> findByStatus(MissionStatus status);
}