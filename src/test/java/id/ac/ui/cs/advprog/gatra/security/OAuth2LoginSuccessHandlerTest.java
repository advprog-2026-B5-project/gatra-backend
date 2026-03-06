package id.ac.ui.cs.advprog.gatra.security;

import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private id.ac.ui.cs.advprog.gatra.repository.StudentProfileRepository studentProfileRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private OAuth2LoginSuccessHandler handler;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("test@gmail.com");
    }

    @Test
    void testOnAuthenticationSuccess_UserExists() throws Exception {
        // Mock data dari Google
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@gmail.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Test User");

        // Mock database & JWT
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("dummy-token");

        when(response.encodeRedirectURL(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        // Jalankan fungsi
        handler.onAuthenticationSuccess(request, response, authentication);

        // Pastikan tidak ada save user baru dan fungsi redirect dipanggil
        verify(userRepository, never()).save(any(User.class));
        verify(response).sendRedirect(contains("token=dummy-token"));
    }

    @Test
    void testOnAuthenticationSuccess_NewUser() throws Exception {
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("newuser@gmail.com");
        when(oAuth2User.getAttribute("name")).thenReturn("New User");

        // User tidak ditemukan, pura-pura save berhasil
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(studentProfileRepository.save(any())).thenReturn(null);

        when(jwtUtil.generateToken(any(User.class))).thenReturn("dummy-token-new");

        when(response.encodeRedirectURL(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        handler.onAuthenticationSuccess(request, response, authentication);

        // Pastikan userRepository.save dipanggil karena ini user baru
        verify(userRepository, times(1)).save(any(User.class));
        verify(response).sendRedirect(contains("token=dummy-token-new"));
    }
}