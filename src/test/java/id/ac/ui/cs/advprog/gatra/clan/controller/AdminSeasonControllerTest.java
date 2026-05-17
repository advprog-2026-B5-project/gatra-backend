package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.scoring.service.SeasonService;
import id.ac.ui.cs.advprog.gatra.auth.security.JwtUtil;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminSeasonController.class)
@AutoConfigureMockMvc(addFilters = false) // Menyesuaikan dengan konfigurasi test Anda
class AdminSeasonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeasonService seasonService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        // Setup jika diperlukan
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void resetSeason_ShouldReturnOk_WhenSuccess() throws Exception {
        // Arrange
        doNothing().when(seasonService).resetSeason();

        // Act & Assert
        mockMvc.perform(post("/api/admin/season/reset")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Season reset successfully. All leaderboards are now 0."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void resetSeason_ShouldReturnInternalServerError_WhenExceptionThrown() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(seasonService).resetSeason();

        // Act & Assert
        mockMvc.perform(post("/api/admin/season/reset")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to reset season: Database error"));
    }
}