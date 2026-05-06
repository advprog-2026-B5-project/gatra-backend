package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.achievement.controller.AdminAchievementController;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.service.AchievementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAchievementControllerTest {

    private static final String ACHIEVEMENT_NAME = "tes";
    private static final String UPDATED_NAME = "Baca Buku";
    private static final ActionType CATEGORY = ActionType.READ_ARTICLE;
    private static final Integer MILESTONE = 10;
    private static final String DESCRIPTION = "Baca 10 artikel";
    private static final String BADGE_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQm9d2Z1IVPte9tDEJnCkPChtLHbTo8NdZZBg&s";

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private AdminAchievementController achievementController;

    private UUID achievementId;
    private AchievementRequest request;
    private AchievementResponse response;

    @BeforeEach
    void setUp() {
        achievementId = UUID.randomUUID();

        request = new AchievementRequest();
        request.setName(ACHIEVEMENT_NAME);
        request.setCategory(CATEGORY);
        request.setMilestoneThreshold(MILESTONE);
        request.setDescription(DESCRIPTION);
        request.setBadgeUrl(BADGE_URL);

        response = AchievementResponse.builder()
                .id(achievementId)
                .name(ACHIEVEMENT_NAME)
                .category(CATEGORY)
                .milestoneThreshold(MILESTONE)
                .description(DESCRIPTION)
                .badgeUrl(BADGE_URL)
                .build();
    }

    @Test
    void getAllAchievements_shouldReturnOkWithList() {
        when(achievementService.getAllAchievements()).thenReturn(List.of(response));

        ResponseEntity<List<AchievementResponse>> result = achievementController.getAllAchievements();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(achievementService, times(1)).getAllAchievements();
    }

    @Test
    void getAllAchievements_whenEmpty_shouldReturnOkWithEmptyList() {
        when(achievementService.getAllAchievements()).thenReturn(List.of());

        ResponseEntity<List<AchievementResponse>> result = achievementController.getAllAchievements();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void getAchievementById_whenFound_shouldReturnOk() {
        when(achievementService.getAchievementById(achievementId)).thenReturn(response);

        ResponseEntity<AchievementResponse> result = achievementController.getAchievementById(achievementId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(achievementService, times(1)).getAchievementById(achievementId);
    }

    @Test
    void createAchievement_whenValid_shouldReturnOk() {
        when(achievementService.createAchievement(any(AchievementRequest.class))).thenReturn(response);

        ResponseEntity<AchievementResponse> result = achievementController.createAchievement(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(achievementService, times(1)).createAchievement(any(AchievementRequest.class));
    }

    @Test
    void updateAchievement_whenValid_shouldReturnOk() {
        AchievementRequest updateRequest = new AchievementRequest();
        updateRequest.setName(UPDATED_NAME);
        updateRequest.setCategory(CATEGORY);
        updateRequest.setMilestoneThreshold(50);
        updateRequest.setDescription("Baca 50 artikel");
        updateRequest.setBadgeUrl(BADGE_URL);

        AchievementResponse updatedResponse = AchievementResponse.builder()
                .id(achievementId)
                .name(UPDATED_NAME)
                .category(CATEGORY)
                .milestoneThreshold(50)
                .description("Baca 50 artikel")
                .badgeUrl(BADGE_URL)
                .build();

        when(achievementService.updateAchievement(eq(achievementId), any(AchievementRequest.class)))
                .thenReturn(updatedResponse);

        ResponseEntity<AchievementResponse> result = achievementController.updateAchievement(achievementId, updateRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(UPDATED_NAME, result.getBody().getName());
        verify(achievementService, times(1)).updateAchievement(eq(achievementId), any(AchievementRequest.class));
    }

    @Test
    void deleteAchievement_whenFound_shouldReturnNoContent() {
        doNothing().when(achievementService).deleteAchievement(achievementId);

        ResponseEntity<Void> result = achievementController.deleteAchievement(achievementId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(achievementService, times(1)).deleteAchievement(achievementId);
    }
}