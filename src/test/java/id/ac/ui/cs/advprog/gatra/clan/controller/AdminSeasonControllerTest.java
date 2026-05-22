package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.auth.security.JwtUtil;
import id.ac.ui.cs.advprog.gatra.clan.dto.SeasonResultResponse;
import id.ac.ui.cs.advprog.gatra.clan.service.ClanSeasonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminSeasonController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminSeasonControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean private ClanSeasonService clanSeasonService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    void endSeason_success() throws Exception {
        SeasonResultResponse result = SeasonResultResponse.builder()
                .seasonNumber(1)
                .frozenAt(LocalDateTime.now())
                .leaderboards(List.of())
                .build();
        when(clanSeasonService.endSeason()).thenReturn(result);

        mockMvc.perform(post("/api/admin/season/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seasonNumber").value(1));
    }

    @Test
    void endSeason_error() throws Exception {
        when(clanSeasonService.endSeason())
                .thenThrow(new RuntimeException("Gagal"));

        mockMvc.perform(post("/api/admin/season/reset"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
    }
}