package id.ac.ui.cs.advprog.gatra.achievement.controller;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.service.UserAchievementService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentAchievementControllerTest {

    @Mock
    private UserAchievementService userAchievementService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private StudentAchievementController studentController;

    private final String USERNAME = "roselia.evanny";
    private UUID achievementId;

    @BeforeEach
    void setUp() {
        achievementId = UUID.randomUUID();
        when(userDetails.getUsername()).thenReturn(USERNAME);
    }

    @Test
    void getMyAchievements_shouldReturnOk() {
        AchievementResponse response = AchievementResponse.builder().name("My Achievement").build();
        when(userAchievementService.getMyAchievements(USERNAME)).thenReturn(List.of(response));

        ResponseEntity<List<AchievementResponse>> result = studentController.getMyAchievements(userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(userAchievementService, times(1)).getMyAchievements(USERNAME);
    }

    @Test
    void toggleDisplayAchievement_shouldReturnNoContent() {
        doNothing().when(userAchievementService).toggleDisplayAchievement(USERNAME, achievementId, true);

        ResponseEntity<Void> result = studentController.toggleDisplayAchievement(achievementId, true, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(userAchievementService, times(1)).toggleDisplayAchievement(USERNAME, achievementId, true);
    }
}