package id.ac.ui.cs.advprog.gatra.service;
import id.ac.ui.cs.advprog.gatra.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.dto.DailyMissionResponse;
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