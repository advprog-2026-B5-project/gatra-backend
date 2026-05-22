package id.ac.ui.cs.advprog.gatra.auth.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.gatra.auth.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Functional: Should successfully register a new user and login")
    void testCompleteAuthenticationFlow() throws Exception {
        // 1. Persiapkan Data Registrasi
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser_functional");
        registerRequest.setEmail("testuser_functional@ui.ac.id");
        registerRequest.setPassword("Password123!");
        registerRequest.setDisplayName("Test User Gatra");

        // 2. Eksekusi Registrasi (Harapannya 201 Created)
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser_functional"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("ROLE_STUDENT"));

        // 3. Persiapkan Data Login (menggunakan data yang baru saja diregistrasi)
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("testuser_functional");
        loginRequest.setPassword("Password123!");

        // 4. Eksekusi Login (Harapannya 200 OK dan mendapatkan Token JWT)
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists()) // Memastikan JWT Token digenerate
                .andExpect(jsonPath("$.username").value("testuser_functional"))
                .andExpect(jsonPath("$.role").value("ROLE_STUDENT"));
    }
}