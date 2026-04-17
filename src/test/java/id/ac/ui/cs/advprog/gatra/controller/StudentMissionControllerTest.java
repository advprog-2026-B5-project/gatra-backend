package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.service.DailyMissionService;
import id.ac.ui.cs.advprog.gatra.security.JwtUtil;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentMissionController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentMissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DailyMissionService dailyMissionService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "STUDENT")
    void getActiveMissions_ShouldReturnListOfMissions() throws Exception {
        UUID missionId = UUID.randomUUID();
        DailyMissionResponse response = DailyMissionResponse.builder()
                .id(missionId)
                .title("Baca Artikel Populer")
                .rewardPoints(20)
                .actionType("READ_ARTICLE")
                .status("ACTIVE")
                .build();

        when(dailyMissionService.getActiveMissions()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/daily-missions/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(missionId.toString()))
                .andExpect(jsonPath("$[0].title").value("Baca Artikel Populer"))
                .andExpect(jsonPath("$[0].rewardPoints").value(20));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getActiveMissions_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(dailyMissionService.getActiveMissions()).thenReturn(List.of());

        mockMvc.perform(get("/api/daily-missions/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}