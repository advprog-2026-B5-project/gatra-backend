package id.ac.ui.cs.advprog.gatra.achievement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.achievement.service.DailyMissionService;
import id.ac.ui.cs.advprog.gatra.exception.GlobalExceptionHandler;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
@Import(GlobalExceptionHandler.class)
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
    private final String BASE_URL = "/api/admin/missions";

    @BeforeEach
    void setUp() {
        missionId = UUID.randomUUID();

        request = DailyMissionRequest.builder()
                .title("Misi Baca")
                .description("Baca 2 artikel")
                .targetCount(2)
                .rewardPoints(50)
                .actionType("READ_ARTICLE")
                .status("ACTIVE")
                .build();

        response = DailyMissionResponse.builder()
                .id(missionId)
                .title("Misi Baca")
                .description("Baca 2 artikel")
                .targetCount(2)
                .rewardPoints(50)
                .actionType("READ_ARTICLE")
                .status("ACTIVE")
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMission_ShouldReturnCreated() throws Exception {
        when(dailyMissionService.createMission(any(DailyMissionRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(missionId.toString()))
                .andExpect(jsonPath("$.title").value("Misi Baca"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMission_ShouldReturnBadRequest_WhenInvalid() throws Exception {
        DailyMissionRequest invalidRequest = DailyMissionRequest.builder()
                .title("")
                .description("")
                .targetCount(0)
                .rewardPoints(-1)
                .actionType("")
                .status("")
                .build();

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllMissions_ShouldReturnList() throws Exception {
        when(dailyMissionService.getAllMissions()).thenReturn(List.of(response));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Misi Baca"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMissionById_ShouldReturnOk_WhenFound() throws Exception {
        when(dailyMissionService.getMissionById(missionId)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/{id}", missionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(missionId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMissionById_ShouldReturnNotFound_WhenMissionDoesNotExist() throws Exception {
        when(dailyMissionService.getMissionById(missionId))
                .thenThrow(new ResourceNotFoundException("DailyMission", missionId));

        mockMvc.perform(get(BASE_URL + "/{id}", missionId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMission_ShouldReturnOk() throws Exception {
        when(dailyMissionService.updateMission(eq(missionId), any(DailyMissionRequest.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/{id}", missionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Misi Baca"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMission_ShouldReturnBadRequest_WhenInvalid() throws Exception {
        DailyMissionRequest invalidRequest = DailyMissionRequest.builder()
                .title("")
                .build();

        mockMvc.perform(put(BASE_URL + "/{id}", missionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMission_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", missionId))
                .andExpect(status().isNoContent());
    }
}