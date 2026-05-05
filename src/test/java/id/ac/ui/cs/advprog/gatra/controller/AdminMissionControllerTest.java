package id.ac.ui.cs.advprog.gatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.achievement.controller.AdminMissionController;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.security.JwtUtil;
import id.ac.ui.cs.advprog.gatra.achievement.service.DailyMissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminMissionController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminMissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DailyMissionService dailyMissionService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private DailyMissionRequest request;
    private DailyMissionResponse response;
    private UUID missionId;

    @BeforeEach
    void setUp() {
        missionId = UUID.randomUUID();
        request = DailyMissionRequest.builder()
                .title("Misi Kuis")
                .description("Selesaikan kuis")
                .targetCount(1)
                .rewardPoints(100)
                .actionType("FINISH_QUIZ")
                .status("ACTIVE")
                .build();

        response = DailyMissionResponse.builder()
                .id(missionId)
                .title("Misi Kuis")
                .description("Selesaikan kuis")
                .targetCount(1)
                .rewardPoints(100)
                .actionType("FINISH_QUIZ")
                .status("ACTIVE")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMission_ShouldReturnCreated() throws Exception {
        when(dailyMissionService.createMission(any(DailyMissionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/admin/daily-missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rewardPoints").value(100))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllMissions_ShouldReturnList() throws Exception {
        when(dailyMissionService.getAllMissions()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/admin/daily-missions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(missionId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMission_ShouldReturnOk() throws Exception {
        when(dailyMissionService.updateMission(eq(missionId), any(DailyMissionRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/admin/daily-missions/{id}", missionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Misi Kuis"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMission_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/admin/daily-missions/{id}", missionId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMissionById_ShouldReturn200Ok_WhenMissionExists() throws Exception {
        // Arrange: Mock service agar mengembalikan response DTO
        when(dailyMissionService.getMissionById(missionId)).thenReturn(response);

        // Act & Assert: Melakukan request GET dan verifikasi JSON
        mockMvc.perform(get("/api/admin/daily-missions/{id}", missionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(missionId.toString()))
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.rewardPoints").value(response.getRewardPoints()))
                .andExpect(jsonPath("$.status").value(response.getStatus()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMissionById_ShouldReturn404NotFound_WhenMissionDoesNotExist() throws Exception {
        // Arrange: Mock service agar melempar exception
        when(dailyMissionService.getMissionById(missionId))
                .thenThrow(new ResourceNotFoundException("DailyMission", missionId));

        // Act & Assert: Pastikan status HTTP yang kembali adalah 404
        mockMvc.perform(get("/api/admin/daily-missions/{id}", missionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}