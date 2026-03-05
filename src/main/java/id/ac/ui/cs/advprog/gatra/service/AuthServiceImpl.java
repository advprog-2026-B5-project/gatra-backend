package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.model.Role;
import id.ac.ui.cs.advprog.gatra.model.AuthProvider;
import id.ac.ui.cs.advprog.gatra.model.StudentProfile;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.dto.RegisterRequest;
import id.ac.ui.cs.advprog.gatra.dto.LoginRequest;
import id.ac.ui.cs.advprog.gatra.dto.AuthResponse;
import id.ac.ui.cs.advprog.gatra.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder; // Untuk enkripsi password
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse registerStudent(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username sudah digunakan");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }
        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Nomor HP sudah terdaftar");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .displayName(request.getDisplayName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_STUDENT) // Default register adalah Pelajar
                .provider(AuthProvider.LOCAL) // Login manual
                .build();

        User savedUser = userRepository.save(newUser);

        StudentProfile newProfile = StudentProfile.builder()
                .user(savedUser)
                .totalScore(0L)
                .currentLevel(1)
                .currentLeagueTier("Bronze") // Default rank bronze
                .isClanLeader(false)
                .build();

        studentProfileRepository.save(newProfile);

        String jwtToken = jwtUtil.generateToken(savedUser);

        // Response ke Frontend
        return AuthResponse.builder()
                .userId(savedUser.getId().toString())
                .username(savedUser.getUsername())
                .role(savedUser.getRole().name())
                .token(jwtToken) // Token disimpan React di localStorage
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUserIdentifier(request.getIdentifier())
                .orElseThrow(() -> new BadCredentialsException("Email atau Nomor HP tidak ditemukan"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Email/Nomor HP atau Password salah");
        }

        String jwtToken = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .role(user.getRole().name())
                .token(jwtToken) // Token disimpan React di localStorage
                .build();
    }
}
