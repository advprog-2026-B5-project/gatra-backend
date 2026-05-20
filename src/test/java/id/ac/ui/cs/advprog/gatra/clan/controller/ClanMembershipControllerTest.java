package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.MembershipDecisionRequest;
import id.ac.ui.cs.advprog.gatra.clan.dto.MembershipResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.service.ClanMembershipService;
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

@WebMvcTest(controllers = ClanMembershipController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClanMembershipControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private ClanMembershipService membershipService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsService userDetailsService;

    @Test
    void apply_success() throws Exception {
        MembershipResponse res = MembershipResponse.builder().id("mem1").build();
        when(membershipService.applyToClan("1", "user1")).thenReturn(res);

        mockMvc.perform(post("/clans/1/applications").requestAttr("userId", "user1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("mem1"));
    }

    @Test
    void getPending_success() throws Exception {
        MembershipResponse res = MembershipResponse.builder().id("mem1").build();
        when(membershipService.getPendingApplications("1", "leader1")).thenReturn(List.of(res));

        mockMvc.perform(get("/clans/1/applications").requestAttr("userId", "leader1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("mem1"));
    }

    @Test
    void decide_success() throws Exception {
        MembershipDecisionRequest req = new MembershipDecisionRequest();
        req.setDecision(MembershipStatus.APPROVED);

        MembershipResponse res = MembershipResponse.builder().status(MembershipStatus.APPROVED).build();
        when(membershipService.decideMembership(eq("1"), eq("app1"), any(), eq("leader1"))).thenReturn(res);

        mockMvc.perform(patch("/clans/1/applications/app1")
                .requestAttr("userId", "leader1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void leaveClan_success() throws Exception {
        doNothing().when(membershipService).leaveClan("1", "user1");

        mockMvc.perform(delete("/clans/1/applications").requestAttr("userId", "user1"))
                .andExpect(status().isNoContent());
    }
}