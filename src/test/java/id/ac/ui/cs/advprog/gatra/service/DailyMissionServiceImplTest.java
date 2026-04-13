package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.repository.DailyMissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
                .title("Baca Berita")
                .description("Membaca 3 berita")
                .targetCount(3)
                .actionType("READ_ARTICLE")
                .isActive(true)
                .build();

        request = DailyMissionRequest.builder()
                .title("Baca Berita Baru")
                .description("Membaca 5 berita")
                .targetCount(5)
                .actionType("READ_ARTICLE")
                .isActive(false)
                .build();
    }

    @Test
    void createMission_ShouldReturnSavedMission() {
        when(missionRepository.save(any(DailyMission.class))).thenReturn(mission);

        DailyMissionResponse response = missionService.createMission(request);

        assertNotNull(response);
        assertEquals("Baca Berita", response.getTitle());
        verify(missionRepository, times(1)).save(any(DailyMission.class));
    }

    @Test
    void getMissionById_ShouldReturnMission() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));

        DailyMissionResponse response = missionService.getMissionById(missionId);

        assertNotNull(response);
        assertEquals(missionId, response.getId());
    }

    @Test
    void getMissionById_ShouldThrowException_WhenNotFound() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> missionService.getMissionById(missionId));
    }

    @Test
    void updateMission_ShouldReturnUpdatedMission() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(missionRepository.save(any(DailyMission.class))).thenReturn(mission);

        DailyMissionResponse response = missionService.updateMission(missionId, request);

        assertNotNull(response);
        verify(missionRepository, times(1)).save(mission);
    }

    @Test
    void deleteMission_ShouldCallRepositoryDelete() {
        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));

        missionService.deleteMission(missionId);

        verify(missionRepository, times(1)).delete(mission);
    }

    @Test
    void getAllMissions_ShouldReturnListOfMissions() {
        // Arrange
        when(missionRepository.findAll()).thenReturn(List.of(mission));

        // Act
        List<DailyMissionResponse> responses = missionService.getAllMissions();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Baca Berita", responses.get(0).getTitle());
        verify(missionRepository, times(1)).findAll();
    }

    @Test
    void updateMission_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(missionRepository.findById(missionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> missionService.updateMission(missionId, request));
        verify(missionRepository, never()).save(any());
    }

    @Test
    void deleteMission_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(missionRepository.findById(missionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> missionService.deleteMission(missionId));
        verify(missionRepository, never()).delete(any());
    }
}