package id.ac.ui.cs.advprog.gatra.auth.service;

import id.ac.ui.cs.advprog.gatra.auth.model.Role;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.model.StudentProfile;
import id.ac.ui.cs.advprog.gatra.auth.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.auth.dto.UserResponse;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

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

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

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

    @Test
    void testGetUserById_Success() {
        UUID dummyId = dummyUser.getId();
        Mockito.when(userRepository.findById(dummyId)).thenReturn(Optional.of(dummyUser));

        UserResponse response = userService.getUserById(dummyId);

        assertNotNull(response);
        assertEquals(dummyUser.getUsername(), response.getUsername());
        assertEquals(dummyUser.getEmail(), response.getEmail());
    }

    @Test
    void testGetUserById_Failed_NotFound() {
        UUID dummyId = UUID.randomUUID();
        Mockito.when(userRepository.findById(dummyId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(dummyId);
        });

        assertEquals("User tidak ditemukan", exception.getMessage());
    }

    @Test
    void getUserEntityById_ShouldReturnUser_WhenFound() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("user")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserEntityById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("user", result.getUsername());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserEntityById_ShouldThrowException_WhenNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserEntityById(userId);
        });

        assertEquals("User tidak ditemukan", exception.getMessage());
    }

    @Test
    void getUserEntityByUsername_ShouldReturnUser_WhenFound() {
        String username = "user2";
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.getUserEntityByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUserEntityByUsername_ShouldThrowException_WhenNotFound() {
        String username = "unknown_user";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserEntityByUsername(username);
        });
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userId, "New Name", "08123");
        });

        assertEquals("User tidak ditemukan", exception.getMessage());
    }

    @Test
    void updateUser_ShouldOnlyUpdateDisplayName_WhenPhoneIsBlank() {
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder().id(userId).displayName("Old").phoneNumber("123").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateUser(userId, "New Name", "  "); // Blank phone

        assertEquals("New Name", updatedUser.getDisplayName());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_ShouldOnlyUpdatePhone_WhenDisplayNameIsNull() {
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder().id(userId).displayName("Old").phoneNumber("123").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateUser(userId, null, "999"); // Null name

        assertEquals("999", updatedUser.getPhoneNumber());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenFound() {
        UUID userId = UUID.randomUUID();
        User mockUser = User.builder()
                .id(userId)
                .username("testuser")
                .role(Role.ROLE_STUDENT)
                .build();

        StudentProfile mockProfile = StudentProfile.builder()
                .user(mockUser)
                .currentLeagueTier("Silver")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(studentProfileRepository.findById(userId)).thenReturn(Optional.of(mockProfile));
        when(pointHistoryRepository.sumPointsByUserId(userId.toString())).thenReturn(150.5);

        UserResponse response = userService.getUserById(userId);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals(151, response.getTotalScore()); // Tests the Math.round()
        assertEquals("Silver", response.getCurrentLeagueTier());
    }

    @Test
    void getUserById_ShouldReturnDefaultBronze_WhenProfileNotFound() {
        UUID userId = UUID.randomUUID();
        User mockUser = User.builder()
                .id(userId)
                .username("testuser")
                .role(Role.ROLE_STUDENT)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(studentProfileRepository.findById(userId)).thenReturn(Optional.empty()); // No profile
        when(pointHistoryRepository.sumPointsByUserId(userId.toString())).thenReturn(0.0);

        UserResponse response = userService.getUserById(userId);

        assertEquals("Bronze", response.getCurrentLeagueTier());
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        List<UserResponse> responses = userService.getAllUsers();

        assertTrue(responses.isEmpty());
    }

    @Test
    void getAllUsers_ShouldReturnMappedResponses_WithBatchData() {
        // 1. Setup Mock Users
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();

        User user1 = User.builder().id(user1Id).username("user1").role(Role.ROLE_STUDENT).build();
        User user2 = User.builder().id(user2Id).username("user2").role(Role.ROLE_STUDENT).build();
        List<User> mockUsers = List.of(user1, user2);

        // 2. Setup Mock Profiles
        StudentProfile profile1 = StudentProfile.builder().user(user1).currentLeagueTier("Gold").build();
        // user2 intentionally has no profile to test the fallback

        // 3. Setup Mock Scores for the bulk query
        List<Object[]> mockBulkScores = new java.util.ArrayList<>();
        mockBulkScores.add(new Object[]{user1Id.toString(), 500.0});
        // user2 score intentionally missing to test the getOrDefault fallback

        // 4. Mock Repository Calls
        when(userRepository.findAll()).thenReturn(mockUsers);
        when(studentProfileRepository.findAllById(any())).thenReturn(List.of(profile1));
        when(pointHistoryRepository.sumPointsByUserIdsBulk(any())).thenReturn(mockBulkScores);

        // 5. Execute
        List<UserResponse> responses = userService.getAllUsers();

        // 6. Verify
        assertEquals(2, responses.size());

        // Verify User 1 mapping
        UserResponse res1 = responses.stream().filter(r -> r.getId().equals(user1Id)).findFirst().get();
        assertEquals("Gold", res1.getCurrentLeagueTier());
        assertEquals(500, res1.getTotalScore());

        // Verify User 2 fallback mapping
        UserResponse res2 = responses.stream().filter(r -> r.getId().equals(user2Id)).findFirst().get();
        assertEquals("Bronze", res2.getCurrentLeagueTier());
        assertEquals(0, res2.getTotalScore());
    }

    @Test
    void deleteUserById_ShouldNotDeleteProfile_WhenUserIsAdmin() {
        UUID adminId = UUID.randomUUID();
        User adminUser = User.builder()
                .id(adminId)
                .role(Role.ROLE_ADMIN) // Not a student!
                .build();

        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));

        userService.deleteUserById(adminId);

        // Verify that we deleted the user...
        verify(userRepository, times(1)).delete(adminUser);

        // ...but we NEVER tried to delete a student profile! (This covers the false branch)
        verify(studentProfileRepository, never()).deleteById(any());
    }

    @Test
    void getUserById_ShouldReturnBronze_WhenProfileExistsButTierIsNull() {
        UUID userId = UUID.randomUUID();
        User mockUser = User.builder()
                .id(userId)
                .username("testuser")
                .role(Role.ROLE_STUDENT)
                .build();

        // The missing branch condition: Profile is NOT null, but Tier IS null
        StudentProfile mockProfileWithNullTier = StudentProfile.builder()
                .user(mockUser)
                .currentLeagueTier(null)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(studentProfileRepository.findById(userId)).thenReturn(Optional.of(mockProfileWithNullTier));
        when(pointHistoryRepository.sumPointsByUserId(userId.toString())).thenReturn(0.0);

        UserResponse response = userService.getUserById(userId);

        assertEquals("Bronze", response.getCurrentLeagueTier());
    }
}