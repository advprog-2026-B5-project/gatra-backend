package id.ac.ui.cs.advprog.gatra.achievement.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
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
class AchievementFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Functional: Complete Admin CRUD Flow for Achievements")
    @WithMockUser(roles = "ADMIN") // Sangat penting untuk melewati @PreAuthorize
    void testCompleteAdminAchievementFlow() throws Exception {

        // 1. CREATE (POST)
        AchievementRequest createRequest = new AchievementRequest();
        createRequest.setName("Master of Reading");
        createRequest.setCategory(ActionType.valueOf("READ_ARTICLE"));
        createRequest.setMilestoneThreshold(50);
        createRequest.setDescription("Membaca 50 artikel dalam satu bulan");
        createRequest.setBadgeUrl("https://example.com/badges/reading-master.png");

        MvcResult createResult = mockMvc.perform(post("/api/admin/achievements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk()) // Berdasarkan controller-mu mengembalikan 200 OK
                .andExpect(jsonPath("$.name").value("Master of Reading"))
                .andExpect(jsonPath("$.milestoneThreshold").value(50))
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // Ekstrak ID dari hasil Create untuk digunakan pada tahap Update & Delete
        String responseString = createResult.getResponse().getContentAsString();
        String achievementId = JsonPath.parse(responseString).read("$.id");

        // 2. READ ALL (GET)
        mockMvc.perform(get("/api/admin/achievements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == '" + achievementId + "')]").exists());

        // 3. READ BY ID (GET)
        mockMvc.perform(get("/api/admin/achievements/" + achievementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Master of Reading"))
                .andExpect(jsonPath("$.badgeUrl").value("https://example.com/badges/reading-master.png"));

        // 4. UPDATE (PUT)
        AchievementRequest updateRequest = new AchievementRequest();
        updateRequest.setName("Grandmaster of Reading"); // Ubah nama
        updateRequest.setCategory(ActionType.valueOf("READ_ARTICLE"));
        updateRequest.setMilestoneThreshold(100); // Ubah threshold
        updateRequest.setDescription("Membaca 100 artikel");
        updateRequest.setBadgeUrl("https://example.com/badges/reading-grandmaster.png");

        mockMvc.perform(put("/api/admin/achievements/" + achievementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Grandmaster of Reading"))
                .andExpect(jsonPath("$.milestoneThreshold").value(100));

        // 5. DELETE (DELETE)
        mockMvc.perform(delete("/api/admin/achievements/" + achievementId))
                .andExpect(status().isNoContent()); // Berdasarkan ResponseEntity.noContent().build()

        // Verifikasi bahwa data benar-benar terhapus
         mockMvc.perform(get("/api/admin/achievements/" + achievementId))
                 .andExpect(status().isNotFound());
    }
}