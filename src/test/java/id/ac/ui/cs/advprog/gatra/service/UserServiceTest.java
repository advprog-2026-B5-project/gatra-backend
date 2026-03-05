package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.model.Role;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @InjectMocks
    private UserService userService;

    private User dummyUser;
    private final UUID dummyId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        dummyUser = User.builder()
                .id(dummyId)
                .username("testuser")
                .email("test@yomu.id")
                .phoneNumber("081234567890")
                .displayName("Test User")
                .role(Role.ROLE_STUDENT)
                .build();
    }

    @Test
    void testGetAllUsers() {
        // Arrange (Siapkan skenario)
        when(userRepository.findAll()).thenReturn(List.of(dummyUser));

        // Act (Jalankan fungsi)
        List<UserResponse> responses = userService.getAllUsers();

        // Assert (Pastikan hasilnya sesuai)
        assertEquals(1, responses.size());
        assertEquals("testuser", responses.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById(dummyId)).thenReturn(Optional.of(dummyUser));
        when(userRepository.save(any(User.class))).thenReturn(dummyUser);

        User updatedUser = userService.updateUser(dummyId, "Nama Baru", "089999999999");

        assertEquals("Nama Baru", updatedUser.getDisplayName());
        assertEquals("089999999999", updatedUser.getPhoneNumber());
        verify(userRepository).save(dummyUser);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findById(dummyId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(dummyId, "Nama Baru", "089999999999");
        });

        assertEquals("User tidak ditemukan", exception.getMessage());
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById(dummyId)).thenReturn(Optional.of(dummyUser));

        userService.deleteUserById(dummyId);

        // Pastikan profile dihapus dulu, baru usernya
        verify(studentProfileRepository, times(1)).deleteById(dummyId);
        verify(userRepository, times(1)).delete(dummyUser);
    }
}