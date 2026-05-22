package id.ac.ui.cs.advprog.gatra.auth.service;

import id.ac.ui.cs.advprog.gatra.auth.dto.UserResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.auth.model.Role;
import id.ac.ui.cs.advprog.gatra.auth.model.StudentProfile;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import id.ac.ui.cs.advprog.gatra.auth.event.UserDeletedEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MSG = "User tidak ditemukan";

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void deleteUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User dengan ID tersebut tidak ditemukan"));
        if (user.getRole() == Role.ROLE_STUDENT) {
            studentProfileRepository.deleteById(userId);
        }

        eventPublisher.publishEvent(new UserDeletedEvent(userId));

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public User updateUser(UUID userId, String newDisplayName, String newPhoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MSG));

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
        // Fetch all users (Query 1)
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // Extract IDs for our IN clauses
        List<UUID> userIds = users.stream().map(User::getId).toList();
        List<String> userIdStrings = userIds.stream().map(UUID::toString).toList();

        // Batch fetch profiles (Query 2)
        // Convert to a Map for O(1) lookup in memory
        java.util.Map<UUID, StudentProfile> profileMap = studentProfileRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(
                        profile -> profile.getUser().getId(),
                        profile -> profile
                ));

        // Batch fetch scores (Query 3)
        // Convert to a Map for O(1) lookup in memory
        java.util.Map<String, Double> scoreMap = new java.util.HashMap<>();
        List<Object[]> bulkScores = pointHistoryRepository.sumPointsByUserIdsBulk(userIdStrings);
        for (Object[] result : bulkScores) {
            String uid = (String) result[0];
            Double score = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
            scoreMap.put(uid, score);
        }

        // Assemble the final response without touching the database inside the loop
        return users.stream().map(user -> {
            StudentProfile profile = profileMap.get(user.getId());
            double totalUserScore = scoreMap.getOrDefault(user.getId().toString(), 0.0);

            return UserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .displayName(user.getDisplayName())
                    .role(user.getRole())
                    .totalScore(Math.round(totalUserScore))
                    .currentLeagueTier(profile != null && profile.getCurrentLeagueTier() != null
                            ? profile.getCurrentLeagueTier() : "Bronze")
                    .build();
        }).toList();
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MSG));

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
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MSG));
    }

    @Override
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", username));
    }
}