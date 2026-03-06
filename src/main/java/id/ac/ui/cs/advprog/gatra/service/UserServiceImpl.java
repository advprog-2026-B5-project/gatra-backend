package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.UserResponse;
import id.ac.ui.cs.advprog.gatra.model.Role;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
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
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .displayName(user.getDisplayName())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .build();
    }
}