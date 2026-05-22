package id.ac.ui.cs.advprog.gatra.social.service;

import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.auth.dto.PublicProfileResponse;
import id.ac.ui.cs.advprog.gatra.auth.dto.UserSearchResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.auth.model.StudentProfile;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private StudentProfileRepository studentProfileRepository;
    @Mock private PointHistoryRepository pointHistoryRepository;
    @Mock private UserAchievementRepository userAchievementRepository;
    @Mock private ClanMembershipRepository clanMembershipRepository;
    @Mock private AchievementMapper achievementMapper;

    @InjectMocks
    private SocialServiceImpl socialService;

    private User dummyUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        dummyUser = User.builder()
                .id(userId)
                .username("anya_forger")
                .displayName("Anya")
                .build();
    }

    @Test
    void searchUsers_withValidQuery_shouldReturnList() {
        when(userRepository.findByUsernameContainingIgnoreCase("anya"))
                .thenReturn(List.of(dummyUser));

        List<UserSearchResponse> results = socialService.searchUsers("anya");

        assertEquals(1, results.size());
        assertEquals("anya_forger", results.get(0).getUsername());
        assertEquals("Anya", results.get(0).getDisplayName());
        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase("anya");
    }

    @Test
    void searchUsers_withEmptyQuery_shouldReturnEmptyList() {
        List<UserSearchResponse> results = socialService.searchUsers("   ");

        assertTrue(results.isEmpty());
        verify(userRepository, never()).findByUsernameContainingIgnoreCase(any());
    }

    @Test
    void getPublicProfile_Success() {
        String username = "anya";
        User mockUser = User.builder().id(userId).username(username).build();
        StudentProfile mockProfile = StudentProfile.builder().currentLeagueTier("Gold").build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(studentProfileRepository.findById(userId)).thenReturn(Optional.of(mockProfile));
        when(pointHistoryRepository.sumPointsByUserId(userId.toString())).thenReturn(150.0);

        // FIX: Update this mock to use findByUserId... and pass the mockUser.getId()
        when(userAchievementRepository.findByUserIdAndIsDisplayedTrue(userId))
                .thenReturn(Collections.emptyList());

        // (If you test clans, mock clanMembershipRepository.findFirstByUserIdAndStatus here too)

        PublicProfileResponse response = socialService.getPublicProfile(username);

        assertNotNull(response);
        assertEquals(username, response.getUsername());
        assertEquals("Gold", response.getCurrentLeagueTier());
    }

    @Test
    void getPublicProfile_whenUserDoesNotExist_shouldThrowException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> socialService.getPublicProfile("unknown"));

        verify(studentProfileRepository, never()).findById(any());
    }
}