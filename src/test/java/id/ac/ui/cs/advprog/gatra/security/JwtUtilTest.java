package id.ac.ui.cs.advprog.gatra.security;

import id.ac.ui.cs.advprog.gatra.model.Role;
import id.ac.ui.cs.advprog.gatra.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User mockUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Memasukkan secret key secara paksa menggunakan Reflection (karena @Value tidak jalan di Unit Test murni)
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "KunciRahasiaGatraYangSangatPanjangDanAman1234567890!");

        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 86400000L);

        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setUsername("anya");
        mockUser.setEmail("anya@gatra.com");
        mockUser.setRole(Role.ROLE_STUDENT);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(mockUser);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken(mockUser);
        String extractedUsername = jwtUtil.extractUsername(token); // Sesuaikan jika Anda meng-extract email/ID
        assertEquals(mockUser.getUsername(), extractedUsername);
    }

    @Test
    void testValidateToken_Valid() {
        String token = jwtUtil.generateToken(mockUser);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(mockUser.getUsername()) // Asumsi subject JWT Anda adalah username
                .password("password_dummy")       // Password bebas karena tidak dicek oleh JWT
                .authorities(mockUser.getRole().name())
                .build();

        // 3. Masukkan UserDetails ke dalam validasi
        assertTrue(jwtUtil.isTokenValid(token, userDetails));
    }
}