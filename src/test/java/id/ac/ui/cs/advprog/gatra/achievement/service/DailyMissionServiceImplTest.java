package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.DailyMissionMapper;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserMissionProgressRepository;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.achievement.repository.DailyMissionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyMissionServiceImplTest {

    @Mock
    private DailyMissionRepository missionRepository;

    @Mock
    private UserMissionProgressRepository progressRepository;

    @Spy
    private DailyMissionMapper missionMapper;

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
                .rewardPoints(50)
                .actionType(ActionType.READ_ARTICLE)
                .status(MissionStatus.ACTIVE)
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
        assertEquals(missionId, response.getId());
        assertEquals("ACTIVE", response.getStatus());
        verify(missionRepository, times(1)).save(any(DailyMission.class));
        verify(missionMapper, times(1)).toEntity(any());
    }

    @Test
    void getAllMissions_ShouldReturnList() {
        when(missionRepository.findAll()).thenReturn(List.of(mission));

        List<DailyMissionResponse> result = missionService.getAllMissions();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("READ_ARTICLE", result.get(0).getActionType());
    }

    @Test
    void getMissionById_ShouldReturnMission_WhenFound() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));

        DailyMissionResponse response = missionService.getMissionById(missionId);

        assertNotNull(response);
        assertEquals(missionId, response.getId());
        verify(missionRepository, times(1)).findById(missionId);
    }

    @Test
    void getMissionById_ShouldThrowException_WhenNotFound() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> missionService.getMissionById(missionId));
    }

    @Test
    void updateMission_ShouldUpdateExistingData() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(missionRepository.save(any(DailyMission.class))).thenReturn(mission);

        DailyMissionResponse response = missionService.updateMission(missionId, request);

        assertNotNull(response);
        verify(missionMapper).updateEntityFromRequest(eq(request), eq(mission));
        verify(missionRepository).save(mission);
    }

    @Test
    void deleteMission_ShouldCallRepository() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));

        missionService.deleteMission(missionId);

        verify(missionRepository).delete(mission);
    }

    @Test
    void getActiveMissions_ShouldReturnOnlyActiveMissions() {
        when(missionRepository.findByStatus(MissionStatus.ACTIVE)).thenReturn(List.of(mission));

        List<DailyMissionResponse> result = missionService.getActiveMissions();

        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());
    }

    @Test
    void rotateMissions_ShouldRandomizeAndLimitToThree() {
        List<DailyMission> missions = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            missions.add(DailyMission.builder()
                    .title("Misi " + i)
                    .status(MissionStatus.ACTIVE)
                    .build());
        }
        when(missionRepository.findAll()).thenReturn(missions);

        missionService.rotateMissions();

        long activeCount = missions.stream().filter(m -> m.getStatus() == MissionStatus.ACTIVE).count();
        long inactiveCount = missions.stream().filter(m -> m.getStatus() == MissionStatus.INACTIVE).count();

        assertEquals(3, activeCount);
        assertEquals(2, inactiveCount);
        verify(missionRepository).saveAll(missions);
    }

    @Test
    void rotateMissions_WhenLessThanThree_ShouldActivateAll() {
        List<DailyMission> missions = new ArrayList<>();
        missions.add(DailyMission.builder().status(MissionStatus.INACTIVE).build());
        missions.add(DailyMission.builder().status(MissionStatus.INACTIVE).build());
        
        when(missionRepository.findAll()).thenReturn(missions);

        missionService.rotateMissions();

        long activeCount = missions.stream().filter(m -> m.getStatus() == MissionStatus.ACTIVE).count();
        assertEquals(2, activeCount);
    }
}