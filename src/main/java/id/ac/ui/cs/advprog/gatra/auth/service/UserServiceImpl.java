package id.ac.ui.cs.advprog.gatra.auth.service;

import id.ac.ui.cs.advprog.gatra.auth.dto.UserResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.auth.model.Role;
import id.ac.ui.cs.advprog.gatra.model.StudentProfile;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    @Transactional
    public void deleteUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User dengan ID tersebut tidak ditemukan"));
        if (user.getRole() == Role.ROLE_STUDENT) {
            studentProfileRepository.deleteById(userId);
        }
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public User updateUser(UUID userId, String newDisplayName, String newPhoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        Optional.ofNullable(newDisplayName)
                .filter(s -> !s.trim().isEmpty())
                .ifPresent(user::setDisplayName);

        Optional.ofNullable(newPhoneNumber)
                .filter(s -> !s.trim().isEmpty())
                .ifPresent(user::setPhoneNumber);

        return userRepository.save(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    // Fetch profile for the league tier (if it exists)
                    StudentProfile profile = studentProfileRepository.findById(user.getId()).orElse(null);
                    // Fetch dynamic score from the point history ledger
                    double totalUserScore = pointHistoryRepository.sumPointsByUserId(user.getId().toString());

                    return UserResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .phoneNumber(user.getPhoneNumber())
                            .displayName(user.getDisplayName())
                            .role(user.getRole())
                            .totalScore(Math.round(totalUserScore))
                            .currentLeagueTier(profile != null && profile.getCurrentLeagueTier() != null ? profile.getCurrentLeagueTier() : "Bronze")
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        // Fetch profile for the league tier (if it exists)
        StudentProfile profile = studentProfileRepository.findById(id).orElse(null);
        // Fetch dynamic score from the point history ledger
        double totalUserScore = pointHistoryRepository.sumPointsByUserId(user.getId().toString());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .totalScore(Math.round(totalUserScore))
                .currentLeagueTier(profile != null && profile.getCurrentLeagueTier() != null ? profile.getCurrentLeagueTier() : "Bronze")
                .build();
    }

    @Override
    public User getUserEntityById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));
        return user;
    }

    @Override
    public User getUserEntityByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
        return user;
    }
}