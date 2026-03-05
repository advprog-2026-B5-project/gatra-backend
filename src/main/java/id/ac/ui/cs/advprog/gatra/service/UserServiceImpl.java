package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.model.Role;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import id.ac.ui.cs.advprog.gatra.dto.UserResponse;
import java.util.List;
import java.util.stream.Collectors;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Transactional
    public void deleteUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User dengan ID tersebut tidak ditemukan"));

        // Hapus Profile-nya terlebih dahulu (Mencegah Error Foreign Key)
        if (user.getRole() == Role.ROLE_STUDENT) {
            studentProfileRepository.deleteById(userId);
        }

        userRepository.delete(user);
    }

    @Transactional
    public User updateUser(UUID userId, String newDisplayName, String newPhoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        if (newDisplayName != null && !newDisplayName.trim().isEmpty()) {
            user.setDisplayName(newDisplayName);
        }
        if (newPhoneNumber != null && !newPhoneNumber.trim().isEmpty()) {
            user.setPhoneNumber(newPhoneNumber);
        }

        // 3. Simpan perubahan ke database
        return userRepository.save(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        // Map setiap entitas User menjadi UserResponse DTO
        return users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .build()
        ).collect(Collectors.toList());
    }
}