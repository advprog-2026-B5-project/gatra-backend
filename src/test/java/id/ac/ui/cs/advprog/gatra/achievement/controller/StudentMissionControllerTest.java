package id.ac.ui.cs.advprog.gatra.achievement.controller;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.achievement.service.MissionProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentMissionControllerTest {

    @Mock
    private MissionProgressService missionProgressService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private StudentMissionController studentMissionController;

    private UUID missionId;
    private String username;

    @BeforeEach
    void setUp() {
        missionId = UUID.randomUUID();
        username = "roselia.evanny";
    }

    @Test
    void getMyMissions_shouldReturnOkWithList() {
        MissionProgressResponse response = MissionProgressResponse.builder()
                .missionId(missionId)
                .title("Misi Gatra")
                .currentCount(2)
                .targetCount(5)
                .isCompleted(false)
                .build();

        when(userDetails.getUsername()).thenReturn(username);
        when(missionProgressService.getActiveMissionsWithProgress(username))
                .thenReturn(List.of(response));

        ResponseEntity<List<MissionProgressResponse>> result = studentMissionController.getMyMissions(userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("Misi Gatra", result.getBody().get(0).getTitle());
        verify(missionProgressService, times(1)).getActiveMissionsWithProgress(username);
    }

    @Test
    void getMyMissions_whenNoMissions_shouldReturnOkWithEmptyList() {
        when(userDetails.getUsername()).thenReturn(username);
        when(missionProgressService.getActiveMissionsWithProgress(username))
                .thenReturn(List.of());

        ResponseEntity<List<MissionProgressResponse>> result = studentMissionController.getMyMissions(userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void claimReward_shouldReturnOk() {
        MissionProgressResponse response = MissionProgressResponse.builder()
                .missionId(missionId)
                .isCompleted(true)
                .isClaimed(true)
                .build();

        when(userDetails.getUsername()).thenReturn(username);
        when(missionProgressService.claimReward(username, missionId))
                .thenReturn(response);

        ResponseEntity<MissionProgressResponse> result = studentMissionController.claimReward(missionId, userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getIsClaimed());
        verify(missionProgressService, times(1)).claimReward(username, missionId);
    }
}