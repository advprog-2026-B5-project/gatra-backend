package id.ac.ui.cs.advprog.gatra.clan.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.auth.model.Role;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.clan.dto.CreateClanRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClanFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Functional: Complete Clan Flow (Create, Get, Get All, Get My Clan, Delete)")
    @WithMockUser(username = "gatra_leader", roles = "STUDENT") // Agar lolos Spring Security
    void testCompleteClanFlow() throws Exception {

        
        // 0. PERSIAPAN DATA (SEEDING) USER
        User leader = new User();
        leader.setUsername("gatra_leader");
        leader.setEmail("leader@gatra.com");
        leader.setPassword("rahasia123");
        leader.setDisplayName("Sang Ketua Clan");
        leader.setRole(Role.ROLE_STUDENT);
        leader = userRepository.save(leader);

        String leaderId = leader.getId().toString();

        
        // 1. CREATE CLAN (POST)
        CreateClanRequest createReq = new CreateClanRequest();
        createReq.setName("Garuda Fighters");
        createReq.setDescription("Clan untuk para pejuang Gatra");

        MvcResult clanResult = mockMvc.perform(post("/clans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq))
                        .requestAttr("userId", leaderId))
                .andExpect(status().isCreated()) // Sesuai dengan ResponseEntity.status(HttpStatus.CREATED)
                .andExpect(jsonPath("$.name").value("Garuda Fighters"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // Ekstrak ID Clan untuk request selanjutnya
        String clanResponseStr = clanResult.getResponse().getContentAsString();
        String clanId = JsonPath.parse(clanResponseStr).read("$.id");

        
        // 2. GET CLAN BY ID (GET)
        mockMvc.perform(get("/clans/" + clanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Garuda Fighters"));

        
        // 3. GET MY CLAN (GET)
        mockMvc.perform(get("/clans/me")
                        .requestAttr("userId", leaderId)) // Inject userId sebagai Request Attribute
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Garuda Fighters"));

        
        // 4. GET ALL CLANS (GET)
        mockMvc.perform(get("/clans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == '" + clanId + "')]").exists());

        
        // 5. DELETE CLAN (DELETE)
        mockMvc.perform(delete("/clans/" + clanId)
                        .requestAttr("userId", leaderId)) // Perlu userId untuk memvalidasi apakah dia leader
                .andExpect(status().isNoContent()); // Sesuai dengan ResponseEntity.noContent()
    }
}