package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.gatra.clan.service.ClanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import id.ac.ui.cs.advprog.gatra.auth.security.JwtUtil;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(controllers = ClanController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClanControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private ClanService clanService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    void createClan_success() throws Exception {
        CreateClanRequest req = new CreateClanRequest();
        req.setName("Test");
        req.setDescription("Desc");

        ClanResponse res = ClanResponse.builder().id("1").name("Test").build();

        when(clanService.createClan(any(), eq("user1"))).thenReturn(res);

        mockMvc.perform(post("/clans")
                .requestAttr("userId", "user1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void getClan_success() throws Exception {
        ClanResponse res = ClanResponse.builder().id("1").build();
        when(clanService.getClan("1")).thenReturn(res);

        mockMvc.perform(get("/clans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void deleteClan_success() throws Exception {
        doNothing().when(clanService).deleteClan("1", "user1");

        mockMvc.perform(delete("/clans/1").requestAttr("userId", "user1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMyClan_success() throws Exception {
        ClanResponse res = ClanResponse.builder().id("1").build();
        when(clanService.getMyClan("user1")).thenReturn(res);

        mockMvc.perform(get("/clans/me").requestAttr("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void getMyClan_notFound() throws Exception {
        when(clanService.getMyClan("user1")).thenReturn(null);

        mockMvc.perform(get("/clans/me").requestAttr("userId", "user1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllClans_success() throws Exception {
        ClanResponse res = ClanResponse.builder().id("1").build();
        when(clanService.getAllClans()).thenReturn(List.of(res));

        mockMvc.perform(get("/clans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    void kickMember_success() throws Exception {
        doNothing().when(clanService).kickMember("1", "target", "leader1");

        mockMvc.perform(delete("/clans/1/members/target").requestAttr("userId", "leader1"))
                .andExpect(status().isNoContent());
    }
}