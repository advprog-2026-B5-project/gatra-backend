package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.AuthResponse;
import id.ac.ui.cs.advprog.gatra.dto.LoginRequest;
import id.ac.ui.cs.advprog.gatra.dto.RegisterRequest;
import id.ac.ui.cs.advprog.gatra.model.AuthProvider;
import id.ac.ui.cs.advprog.gatra.model.Role;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(UUID.randomUUID())
                .username("anya")
                .email("anya@gatra.com")
                .password("encoded_password")
                .role(Role.ROLE_STUDENT)
                .provider(AuthProvider.LOCAL)
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("anya");
        registerRequest.setEmail("anya@gatra.com");
        registerRequest.setPassword("password123");
        registerRequest.setDisplayName("Anya Forger");

        loginRequest = new LoginRequest();
        loginRequest.setIdentifier("anya@gatra.id");
        loginRequest.setPassword("password123");
    }

    // ==========================================
    // TESTS UNTUK REGISTER
    // ==========================================
    @Test
    void testRegister_Success() {
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(anyString())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        Mockito.when(userRepository.save(any(User.class))).thenReturn(mockUser);

        AuthResponse result = authService.registerStudent(registerRequest);

        assertNotNull(result);
        assertEquals("anya", result.getUsername());
        Mockito.verify(studentProfileRepository, Mockito.times(1)).save(any());
    }

    @Test
    void testRegister_Failed_UsernameAlreadyExists() {
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerStudent(registerRequest);
        });

        assertTrue(exception.getMessage().contains("Username sudah digunakan"));
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }

    // ==========================================
    // TESTS UNTUK LOGIN
    // ==========================================
    @Test
    void testLogin_Success() {
        // Simulasi sukses otentikasi
        Mockito.when(userRepository.findByUserIdentifier(anyString())).thenReturn(Optional.of(mockUser));
        Mockito.when(jwtUtil.generateToken(any(User.class))).thenReturn("dummy.jwt.token");
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("dummy.jwt.token", response.getToken());
    }

    @Test
    void testLogin_Failed_WrongPassword() {
        Mockito.when(userRepository.findByUserIdentifier(anyString())).thenReturn(Optional.of(mockUser));

        // pengecekan password GAGAL / SALAH
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // 3. Ekspektasikan IllegalArgumentException (karena AuthService Anda melempar ini)
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        // 4. Pastikan pesan error-nya sesuai
        assertEquals("Email/Nomor HP atau Password salah", exception.getMessage());
    }
}