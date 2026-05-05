package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.achievement.service.DailyMissionServiceImpl;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.achievement.repository.DailyMissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyMissionServiceImplTest {

    @Mock
    private DailyMissionRepository missionRepository;

    @InjectMocks
    private DailyMissionServiceImpl missionService;

    private DailyMission mission;
    private DailyMissionRequest request;
    private UUID missionId;

    @BeforeEach
    void setUp() {
        missionId = UUID.randomUUID();
        mission = DailyMission.builder()
                .id(missionId)
                .title("Baca Artikel Berita")
                .description("Membaca 3 artikel")
                .targetCount(3)
                .rewardPoints(50) // Sesuai ERD baru
                .actionType(ActionType.READ_ARTICLE) // Sesuai Enum baru
                .status(MissionStatus.ACTIVE) // Sesuai Enum baru
                .build();

        request = DailyMissionRequest.builder()
                .title("Selesaikan Kuis")
                .description("Selesaikan 1 kuis harian")
                .targetCount(1)
                .rewardPoints(100)
                .actionType("FINISH_QUIZ")
                .status("ACTIVE")
                .build();
    }

    @Test
    void createMission_ShouldSaveAndReturnResponse() {
        when(missionRepository.save(any(DailyMission.class))).thenReturn(mission);

        DailyMissionResponse response = missionService.createMission(request);

        assertNotNull(response);
        assertEquals(50, response.getRewardPoints());
        assertEquals("ACTIVE", response.getStatus());
        verify(missionRepository, times(1)).save(any(DailyMission.class));
    }

    @Test
    void getMissionById_ShouldReturnMission_WhenFound() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));

        DailyMissionResponse response = missionService.getMissionById(missionId);

        assertEquals(missionId, response.getId());
        assertEquals("READ_ARTICLE", response.getActionType());
    }

    @Test
    void getMissionById_ShouldThrowException_WhenNotFound() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.empty());

        // Memastikan penggunaan 2 argumen sesuai ResourceNotFoundException.java
        assertThrows(ResourceNotFoundException.class, () -> missionService.getMissionById(missionId));
    }

    @Test
    void getAllMissions_ShouldReturnList() {
        when(missionRepository.findAll()).thenReturn(List.of(mission));

        List<DailyMissionResponse> result = missionService.getAllMissions();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void updateMission_ShouldUpdateExistingData() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(missionRepository.save(any(DailyMission.class))).thenReturn(mission);

        DailyMissionResponse response = missionService.updateMission(missionId, request);

        assertNotNull(response);
        verify(missionRepository).save(any(DailyMission.class));
    }

    @Test
    void deleteMission_ShouldCallRepository() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));

        missionService.deleteMission(missionId);

        verify(missionRepository).delete(mission);
    }

    @Test
    void getActiveMissions_ShouldReturnOnlyActiveMissions() {
        // Arrange
        DailyMission activeMission = DailyMission.builder()
                .title("Misi 1")
                .status(MissionStatus.ACTIVE)
                .actionType(ActionType.READ_ARTICLE)
                .build();

        when(missionRepository.findByStatus(MissionStatus.ACTIVE))
                .thenReturn(List.of(activeMission));

        // Act
        List<DailyMissionResponse> result = missionService.getActiveMissions();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Misi 1", result.get(0).getTitle());
        verify(missionRepository, times(1)).findByStatus(MissionStatus.ACTIVE);
    }

    @Test
    void rotateMissions_WhenMoreThanThree_ShouldActivateExactlyThree() {
        // Arrange: Buat 5 misi di database
        List<DailyMission> missions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            missions.add(DailyMission.builder().title("Misi " + i).status(MissionStatus.ACTIVE).build());
        }
        when(missionRepository.findAll()).thenReturn(missions);

        // Act
        missionService.rotateMissions();

        // Assert: Harus ada tepat 3 yang ACTIVE dan 2 yang INACTIVE
        long activeCount = missions.stream().filter(m -> m.getStatus() == MissionStatus.ACTIVE).count();
        long inactiveCount = missions.stream().filter(m -> m.getStatus() == MissionStatus.INACTIVE).count();

        assertEquals(3, activeCount);
        assertEquals(2, inactiveCount);
        verify(missionRepository, times(1)).saveAll(missions);
    }

    @Test
    void rotateMissions_WhenLessThanThree_ShouldActivateAll() {
        // Arrange: Hanya ada 2 misi di database
        List<DailyMission> missions = new ArrayList<>();
        missions.add(DailyMission.builder().status(MissionStatus.INACTIVE).build());
        missions.add(DailyMission.builder().status(MissionStatus.INACTIVE).build());

        when(missionRepository.findAll()).thenReturn(missions);

        // Act
        missionService.rotateMissions();

        // Assert: Keduanya harus menjadi ACTIVE
        long activeCount = missions.stream().filter(m -> m.getStatus() == MissionStatus.ACTIVE).count();

        assertEquals(2, activeCount);
        verify(missionRepository, times(1)).saveAll(missions);
    }

    @Test
    void rotateMissions_WhenEmpty_ShouldDoNothing() {
        // Arrange
        when(missionRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        missionService.rotateMissions();

        // Assert: Tidak boleh ada error dan tetap memanggil saveAll dengan list kosong
        verify(missionRepository, times(1)).saveAll(anyList());
    }
}