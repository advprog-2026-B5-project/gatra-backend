package id.ac.ui.cs.advprog.gatra.social.functional;

import id.ac.ui.cs.advprog.gatra.auth.model.Role;
import id.ac.ui.cs.advprog.gatra.auth.model.StudentProfile;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SocialFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Functional: Complete Social Flow")
    @WithMockUser(username = "gatra_tester", roles = "STUDENT")
    void testCompleteSocialFlow() throws Exception {

        // 0. SEEDING (DENGAN PEMBERSIHAN SESI)
        // 1. Simpan User 1
        User user1 = new User();
        user1.setUsername("gatra_alpha");
        user1.setEmail("alpha@gatra.com");
        user1.setPassword("rahasia123");
        user1.setDisplayName("Si Paling Alpha");
        user1.setRole(Role.ROLE_STUDENT);
        userRepository.save(user1);

        // 2. Simpan User 2
        User user2 = new User();
        user2.setUsername("gatra_omega");
        user2.setEmail("omega@gatra.com");
        user2.setPassword("rahasia123");
        user2.setDisplayName("Si Paling Omega");
        user2.setRole(Role.ROLE_STUDENT);
        userRepository.save(user2);

        // Flush & Clear untuk memastikan user tersimpan dan sesi bersih
        entityManager.flush();
        entityManager.clear();

        User managedUser1 = userRepository.findById(user1.getId()).orElseThrow();

        // 3. Simpan Profile
        StudentProfile profile = StudentProfile.builder()
                .user(managedUser1)
                .currentLeagueTier("Gold")
                .build();

        studentProfileRepository.save(profile);

        // Flush & Clear lagi sebelum API dipanggil
        entityManager.flush();
        entityManager.clear();

        // 1. TEST PENCARIAN USER
        mockMvc.perform(get("/api/social/search")
                        .param("q", "gatra")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // 2. TEST LIHAT PROFIL PUBLIK
        mockMvc.perform(get("/api/social/profile/gatra_alpha")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("gatra_alpha"))
                .andExpect(jsonPath("$.currentLeagueTier").value("Gold"));
    }
}