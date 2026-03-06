package id.ac.ui.cs.advprog.gatra.security;

import id.ac.ui.cs.advprog.gatra.model.Role;
import id.ac.ui.cs.advprog.gatra.model.StudentProfile;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.model.AuthProvider;
import id.ac.ui.cs.advprog.gatra.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    // AMBIL URL FRONTEND DARI APPLICATION.PROPERTIES
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 1. Ambil data dari Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email tidak ditemukan dari Google");
            return;
        }

        // 2. Cari user di database. Jika tidak ada, buat akun baru
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .username(email.split("@")[0] + UUID.randomUUID().toString().substring(0, 5))
                    .displayName(name)
                    .role(Role.ROLE_STUDENT)
                    .provider(AuthProvider.GOOGLE)
                    .password(UUID.randomUUID().toString())
                    .build();

            newUser = userRepository.save(newUser);

            StudentProfile profile = StudentProfile.builder()
                    .user(newUser)
                    .build();
            studentProfileRepository.save(profile);

            return newUser;
        });

        // 3. Generate JWT Token dan Redirect ke URL dinamis
        String token = jwtUtil.generateToken(user);

        // MENGGUNAKAN VARIABEL FRONTEND URL
        String frontendRedirectUrl = frontendUrl + "/oauth2/redirect?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, frontendRedirectUrl);
    }
}