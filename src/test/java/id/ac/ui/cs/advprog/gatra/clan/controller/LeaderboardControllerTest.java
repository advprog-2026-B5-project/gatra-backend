package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.service.LeaderboardService;
import id.ac.ui.cs.advprog.gatra.auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = LeaderboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class LeaderboardControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private LeaderboardService leaderboardService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    void getAllTierLeaderboards_success() throws Exception {
        TierLeaderboardResponse bronze = TierLeaderboardResponse.builder()
                .tier("BRONZE").rankings(List.of()).build();
        when(leaderboardService.getAllTierLeaderboards()).thenReturn(List.of(bronze));

        mockMvc.perform(get("/clans/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tier").value("BRONZE"));
    }

    @Test
    void getAllTierLeaderboards_empty_returnsOk() throws Exception {
        when(leaderboardService.getAllTierLeaderboards()).thenReturn(List.of());

        mockMvc.perform(get("/clans/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getLeaderboardByTier_validTier_success() throws Exception {
        TierLeaderboardResponse bronze = TierLeaderboardResponse.builder()
                .tier("BRONZE").rankings(List.of()).build();
        when(leaderboardService.getLeaderboardByTier("BRONZE")).thenReturn(bronze);

        mockMvc.perform(get("/clans/leaderboard/BRONZE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier").value("BRONZE"));
    }

    @Test
    void getLeaderboardByTier_invalidTier_returnsBadRequest() throws Exception {
        when(leaderboardService.getLeaderboardByTier("INVALID"))
                .thenThrow(new IllegalArgumentException("No enum constant: INVALID"));

        mockMvc.perform(get("/clans/leaderboard/INVALID"))
                .andExpect(status().isBadRequest());
    }
}