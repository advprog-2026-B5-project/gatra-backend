package id.ac.ui.cs.advprog.gatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.service.DailyMissionService;
import id.ac.ui.cs.advprog.gatra.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminMissionController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass filter keamanan JWT secara default untuk tes unit logic
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
                .title("Kuis Harian")
                .targetCount(1)
                .actionType("FINISH_QUIZ")
                .isActive(true)
                .build();

        response = DailyMissionResponse.builder()
                .id(missionId)
                .title("Kuis Harian")
                .targetCount(1)
                .actionType("FINISH_QUIZ")
                .isActive(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMission_ShouldReturn201() throws Exception {
        when(dailyMissionService.createMission(any(DailyMissionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/admin/daily-missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Kuis Harian"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMissionById_ShouldReturn200() throws Exception {
        when(dailyMissionService.getMissionById(missionId)).thenReturn(response);

        mockMvc.perform(get("/api/admin/daily-missions/{id}", missionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(missionId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllMissions_ShouldReturn200() throws Exception {
        // Arrange
        when(dailyMissionService.getAllMissions()).thenReturn(java.util.List.of(response));

        // Act & Assert
        mockMvc.perform(get("/api/admin/daily-missions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Kuis Harian"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMission_ShouldReturn200() throws Exception {
        // Arrange
        DailyMissionRequest updateRequest = DailyMissionRequest.builder()
                .title("Kuis Harian Updated")
                .targetCount(2)
                .actionType("FINISH_QUIZ")
                .isActive(false)
                .build();

        DailyMissionResponse updateResponse = DailyMissionResponse.builder()
                .id(missionId)
                .title("Kuis Harian Updated")
                .targetCount(2)
                .actionType("FINISH_QUIZ")
                .isActive(false)
                .build();

        when(dailyMissionService.updateMission(eq(missionId), any(DailyMissionRequest.class)))
                .thenReturn(updateResponse);

        // Act & Assert
        mockMvc.perform(put("/api/admin/daily-missions/{id}", missionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Kuis Harian Updated"))
                .andExpect(jsonPath("$.targetCount").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMission_ShouldReturn204() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/admin/daily-missions/{id}", missionId))
                .andExpect(status().isNoContent()); // 204 No Content

        // Verifikasi bahwa service delete dipanggil
        org.mockito.Mockito.verify(dailyMissionService, org.mockito.Mockito.times(1)).deleteMission(missionId);
    }
}