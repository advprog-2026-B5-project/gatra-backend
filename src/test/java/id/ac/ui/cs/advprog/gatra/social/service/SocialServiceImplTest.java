package id.ac.ui.cs.advprog.gatra.social.service;

import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanRole;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.auth.dto.PublicProfileResponse;
import id.ac.ui.cs.advprog.gatra.auth.dto.UserSearchResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.auth.model.StudentProfile;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
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
    void getPublicProfile_whenUserExists_shouldAggregateDataSuccessfully() {
        // Arrange Profile & Score
        StudentProfile profile = StudentProfile.builder().currentLeagueTier("Silver").build();
        when(userRepository.findByUsername("anya_forger")).thenReturn(Optional.of(dummyUser));
        when(studentProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(pointHistoryRepository.sumPointsByUserId(userId.toString())).thenReturn(150.5);

        // Arrange Achievements
        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setAchievement(Achievement.builder().name("First Blood").build());
        AchievementResponse mappedAchievement = AchievementResponse.builder().name("First Blood").build();

        when(userAchievementRepository.findByUserUsernameAndIsDisplayedTrue("anya_forger"))
                .thenReturn(List.of(userAchievement));
        when(achievementMapper.toResponseFromUserAchievement(userAchievement))
                .thenReturn(mappedAchievement);

        // Arrange Clan
        Clan clan = Clan.builder().id("clan-1").name("Eden Academy").build();
        ClanMembership membership = ClanMembership.builder().clan(clan).role(ClanRole.MEMBER).build();
        when(clanMembershipRepository.findFirstByUserIdAndStatus(userId.toString(), MembershipStatus.APPROVED))
                .thenReturn(Optional.of(membership));

        // Act
        PublicProfileResponse result = socialService.getPublicProfile("anya_forger");

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("anya_forger", result.getUsername());
        assertEquals("Silver", result.getCurrentLeagueTier());
        assertEquals(151L, result.getTotalScore()); // 150.5 rounded

        assertEquals(1, result.getFeaturedAchievements().size());
        assertEquals("First Blood", result.getFeaturedAchievements().get(0).getName());

        assertEquals(1, result.getJoinedClans().size());
        assertEquals("Eden Academy", result.getJoinedClans().get(0).getName());
    }

    @Test
    void getPublicProfile_whenUserDoesNotExist_shouldThrowException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> socialService.getPublicProfile("unknown"));

        verify(studentProfileRepository, never()).findById(any());
    }
}