package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.scoring.service.SeasonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import id.ac.ui.cs.advprog.gatra.auth.security.JwtUtil;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(controllers = AdminSeasonController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminSeasonControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private SeasonService seasonService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    void resetSeason_success() throws Exception {
        doNothing().when(seasonService).resetSeason();

        mockMvc.perform(post("/api/admin/season/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void resetSeason_error() throws Exception {
        doThrow(new RuntimeException("Err")).when(seasonService).resetSeason();

        mockMvc.perform(post("/api/admin/season/reset"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
    }
}