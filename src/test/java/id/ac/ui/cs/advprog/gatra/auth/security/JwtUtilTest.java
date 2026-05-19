package id.ac.ui.cs.advprog.gatra.auth.security;

import id.ac.ui.cs.advprog.gatra.auth.model.Role;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
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

    @Test
    void testExtractUserId_Success() {
        String token = jwtUtil.generateToken(mockUser);
        String extractedUserId = jwtUtil.extractUserId(token);

        assertEquals(mockUser.getId().toString(), extractedUserId);
    }

    @Test
    void testIsTokenValid_WrongUser_ReturnsFalse() {
        String token = jwtUtil.generateToken(mockUser);

        // Buat detail user yang berbeda nama dengan token
        org.springframework.security.core.userdetails.UserDetails wrongUser =
                org.springframework.security.core.userdetails.User.builder()
                        .username("bukan_anya")
                        .password("password")
                        .authorities("ROLE_STUDENT")
                        .build();

        assertFalse(jwtUtil.isTokenValid(token, wrongUser));
    }

    @Test
    void testExtractRole_Success() {

        String token = jwtUtil.generateToken(mockUser);

        // 2. Panggil method yang ingin diuji
        String extractedRole = jwtUtil.extractRole(token);

        // 3. Pastikan role yang diekstrak sesuai dengan role milik mockUser
        assertEquals("ROLE_STUDENT", extractedRole);
    }
}