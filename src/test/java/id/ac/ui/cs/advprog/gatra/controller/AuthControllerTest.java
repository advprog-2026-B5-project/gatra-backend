package id.ac.ui.cs.advprog.gatra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.gatra.dto.AuthResponse;
import id.ac.ui.cs.advprog.gatra.dto.LoginRequest;
import id.ac.ui.cs.advprog.gatra.dto.RegisterRequest;
import id.ac.ui.cs.advprog.gatra.model.Role;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.security.JwtUtil;
import id.ac.ui.cs.advprog.gatra.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Matikan filter JWT untuk mempermudah tes controller murni
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    // Menenangkan Spring Security
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==========================================
    // TESTS UNTUK ENDPOINT REGISTER
    // ==========================================
    @Test
    void testRegister_ReturnsCreated() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("anya");
        request.setEmail("anya@gatra.com");
        request.setPassword("password123");
        request.setDisplayName("Anya");

        AuthResponse dummyResponse = new AuthResponse("token", "user", "role", "id");
        Mockito.when(authService.registerStudent(any(RegisterRequest.class))).thenReturn(dummyResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()); // Atau isCreated(), sesuaikan dengan Controller Anda
    }

    @Test
    void testRegister_ReturnsBadRequest_WhenUsernameExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("anya");

        request.setEmail("anya@gatra.com");
        request.setPassword("password123");
        request.setDisplayName("Anya Forger");

        // Sengaja melempar error dari Service
        Mockito.when(authService.registerStudent(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Username sudah digunakan"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username sudah digunakan"));
    }

    // ==========================================
    // TESTS UNTUK ENDPOINT LOGIN
    // ==========================================
    @Test
    void testLogin_ReturnsOk() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("anya@gatra.id");
        request.setPassword("password123");

        AuthResponse authResponse = new AuthResponse("dummy.jwt.token", "anya", "ROLE_STUDENT", UUID.randomUUID().toString());
        Mockito.when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy.jwt.token"));
    }

    @Test
    void testLogin_ReturnsUnauthorized_WhenBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("anya@gatra.id");
        request.setPassword("salah123");

        // Simulasi error salah password
        Mockito.when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Username atau password salah"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()) // HttpStatus 401
                .andExpect(content().string("Username atau password salah"));
    }
}