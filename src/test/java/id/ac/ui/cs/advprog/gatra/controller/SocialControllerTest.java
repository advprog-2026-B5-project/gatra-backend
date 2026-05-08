package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.dto.PublicProfileResponse;
import id.ac.ui.cs.advprog.gatra.dto.UserSearchResponse;
import id.ac.ui.cs.advprog.gatra.security.JwtUtil;
import id.ac.ui.cs.advprog.gatra.service.SocialService;
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

@WebMvcTest(controllers = SocialController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass JWT filters for pure controller testing
class SocialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SocialService socialService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void searchUsers_shouldReturnOkAndList() throws Exception {
        UUID dummyId = UUID.randomUUID();
        UserSearchResponse response = UserSearchResponse.builder()
                .userId(dummyId)
                .username("anya_forger")
                .displayName("Anya")
                .build();

        when(socialService.searchUsers("anya")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/social/search")
                        .param("q", "anya")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("anya_forger"))
                .andExpect(jsonPath("$[0].displayName").value("Anya"));
    }

    @Test
    @WithMockUser
    void getPublicProfile_shouldReturnOkAndProfileData() throws Exception {
        UUID dummyId = UUID.randomUUID();
        PublicProfileResponse profile = PublicProfileResponse.builder()
                .userId(dummyId)
                .username("anya_forger")
                .displayName("Anya")
                .totalScore(500L)
                .currentLeagueTier("Gold")
                .build();

        when(socialService.getPublicProfile("anya_forger")).thenReturn(profile);

        mockMvc.perform(get("/api/social/profile/anya_forger")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("anya_forger"))
                .andExpect(jsonPath("$.totalScore").value(500))
                .andExpect(jsonPath("$.currentLeagueTier").value("Gold"));
    }
}