package id.ac.ui.cs.advprog.gatra.scoring.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.auth.model.Role;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.clan.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.gatra.scoring.model.PointActivityType;
import id.ac.ui.cs.advprog.gatra.scoring.service.PointRecordingService;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Wajib agar database bersih kembali
class ScoringFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    // Kita menyuntikkan Service Scoring secara langsung untuk memasukkan data poin bohongan
    @Autowired
    private PointRecordingService pointRecordingService;

    @Test
    @DisplayName("Functional: Leaderboard Calculation and Scoring Integration")
    @WithMockUser(username = "gatra_scorer", roles = "STUDENT")
    void testCompleteScoringAndLeaderboardFlow() throws Exception {

        
        // 0. PERSIAPAN DATA: Buat User
        User player = new User();
        player.setUsername("gatra_scorer");
        player.setEmail("scorer@gatra.com");
        player.setPassword("rahasia123");
        player.setDisplayName("Pencetak Skor Utama");
        player.setRole(Role.ROLE_STUDENT);
        player = userRepository.save(player);

        String userId = player.getId().toString();

        
        // 1. BUAT CLAN VIA CONTROLLER
        CreateClanRequest createReq = new CreateClanRequest();
        createReq.setName("Scoring Legends");
        createReq.setDescription("Clan khusus untuk testing sistem scoring");

        MvcResult clanResult = mockMvc.perform(post("/clans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq))
                        .requestAttr("userId", userId))
                .andExpect(status().isCreated())
                .andReturn();

        String clanResponseStr = clanResult.getResponse().getContentAsString();
        String clanId = JsonPath.parse(clanResponseStr).read("$.id");

        
        // 2. INJEKSI POIN VIA SERVICE (Bypass Modul Quiz/Mission)
        // Menambahkan 150 poin dari Quiz
        pointRecordingService.recordPoints(userId, clanId, 150.0,
                PointActivityType.QUIZ_PASSED, UUID.randomUUID().toString());

        // Menambahkan 50 poin dari Misi Harian
        pointRecordingService.recordPoints(userId, clanId, 50.0,
                PointActivityType.DAILY_MISSION_COMPLETED, UUID.randomUUID().toString());

        
        // 3. FETCH ALL LEADERBOARDS (GET)
        mockMvc.perform(get("/clans/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Setidaknya harus ada satu data tier yang muncul
                .andExpect(jsonPath("$[0]").exists());

        
        // 4. FETCH LEADERBOARD BY TIER (GET)
        mockMvc.perform(get("/clans/leaderboard/BRONZE"))
                .andExpect(status().isOk());

        
        // 5. TEST EXCEPTION HANDLING (GET DENGAN TIER INVALID)
        mockMvc.perform(get("/clans/leaderboard/TIER_TIDAK_DIKENAL"))
                .andExpect(status().isBadRequest()); // Memastikan status menjadi 400
    }
}