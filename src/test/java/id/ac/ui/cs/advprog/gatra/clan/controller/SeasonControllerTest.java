package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.SeasonResultResponse;
import id.ac.ui.cs.advprog.gatra.clan.service.ClanSeasonService;
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

@WebMvcTest(controllers = SeasonController.class)
@AutoConfigureMockMvc(addFilters = false)
class SeasonControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ClanSeasonService seasonService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    void endSeason_success() throws Exception {
        SeasonResultResponse res = SeasonResultResponse.builder().seasonNumber(1).build();
        when(seasonService.endSeason()).thenReturn(res);

        mockMvc.perform(post("/clans/season/end"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seasonNumber").value(1));
    }

    @Test
    void getLastSeason_success() throws Exception {
        SeasonResultResponse res = SeasonResultResponse.builder().seasonNumber(1).build();
        when(seasonService.getLastSeasonResult()).thenReturn(res);

        mockMvc.perform(get("/clans/season/last"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seasonNumber").value(1));
    }
}