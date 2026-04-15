package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.service.MissionProgressService;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionProgressControllerTest {

    @Mock private MissionProgressService missionProgressService;
    @Mock private UserRepository userRepository;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private MissionProgressController controller;

    private UUID userId;
    private UUID missionId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        missionId = UUID.randomUUID();
        user = User.builder().id(userId).username("student1").build();
    }

    @Test
    void getActiveMissions_shouldReturnOk() {
        MissionProgressResponse response = MissionProgressResponse.builder()
                .missionId(missionId)
                .title("Baca Artikel")
                .currentCount(1)
                .targetCount(3)
                .isCompleted(false)
                .build();

        when(userDetails.getUsername()).thenReturn("student1");
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(user));
        when(missionProgressService.getActiveMissionsWithProgress(userId))
                .thenReturn(List.of(response));

        ResponseEntity<List<MissionProgressResponse>> result = controller.getActiveMissions(userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals(1, result.getBody().get(0).getCurrentCount());
    }

    @Test
    void getActiveMissions_whenEmpty_shouldReturnOkWithEmptyList() {
        when(userDetails.getUsername()).thenReturn("student1");
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(user));
        when(missionProgressService.getActiveMissionsWithProgress(userId))
                .thenReturn(List.of());

        ResponseEntity<List<MissionProgressResponse>> result = controller.getActiveMissions(userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void claimReward_shouldReturnOk() {
        MissionProgressResponse response = MissionProgressResponse.builder()
                .missionId(missionId)
                .currentCount(3)
                .targetCount(3)
                .isClaimed(true)
                .isCompleted(true)
                .build();

        when(userDetails.getUsername()).thenReturn("student1");
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(user));
        when(missionProgressService.claimReward(userId, missionId)).thenReturn(response);

        ResponseEntity<MissionProgressResponse> result = controller.claimReward(missionId, userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().getIsClaimed());
    }
}