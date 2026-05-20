package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.service.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import id.ac.ui.cs.advprog.gatra.auth.security.JwtUtil;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(controllers = LeaderboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class LeaderboardControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private LeaderboardService leaderboardService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    void getAllTierLeaderboards_success() throws Exception {
        TierLeaderboardResponse t = TierLeaderboardResponse.builder().tier("BRONZE").build();
        when(leaderboardService.getAllTierLeaderboards()).thenReturn(List.of(t));

        mockMvc.perform(get("/clans/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tier").value("BRONZE"));
    }

    @Test
    void getLeaderboardByTier_success() throws Exception {
        TierLeaderboardResponse t = TierLeaderboardResponse.builder().tier("BRONZE").build();
        when(leaderboardService.getLeaderboardByTier("BRONZE")).thenReturn(t);

        mockMvc.perform(get("/clans/leaderboard/BRONZE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier").value("BRONZE"));
    }
}