package id.ac.ui.cs.advprog.gatra.achievement.service;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionResponse;
import java.util.List;
import java.util.UUID;

public interface DailyMissionService {
    DailyMissionResponse createMission(DailyMissionRequest request);
    List<DailyMissionResponse> getAllMissions();
    DailyMissionResponse getMissionById(UUID id);
    DailyMissionResponse updateMission(UUID id, DailyMissionRequest request);
    void deleteMission(UUID id);
    List<DailyMissionResponse> getActiveMissions();
    void rotateMissions();
}