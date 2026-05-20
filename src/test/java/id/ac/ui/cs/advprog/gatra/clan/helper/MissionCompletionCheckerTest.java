package id.ac.ui.cs.advprog.gatra.clan.helper;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserMissionProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionCompletionCheckerTest {

    @Mock
    private UserMissionProgressRepository repository;

    @InjectMocks
    private MissionCompletionChecker checker;

    private UUID userId;
    private String userIdStr;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userIdStr = userId.toString();
    }

    @Test
    void hasCompletedAnyMission_returnsTrue_whenAtLeastOneIsClaimed() {
        UserMissionProgress p1 = UserMissionProgress.builder().isClaimed(false).build();
        UserMissionProgress p2 = UserMissionProgress.builder().isClaimed(true).build();

        when(repository.findByUserId(userId)).thenReturn(List.of(p1, p2));

        assertTrue(checker.hasCompletedAnyMission(userIdStr));
    }

    @Test
    void hasCompletedAnyMission_returnsFalse_whenNoneIsClaimed() {
        UserMissionProgress p1 = UserMissionProgress.builder().isClaimed(false).build();

        when(repository.findByUserId(userId)).thenReturn(List.of(p1));

        assertFalse(checker.hasCompletedAnyMission(userIdStr));
    }

    @Test
    void hasCompletedAnyMission_returnsFalse_whenListIsEmpty() {
        when(repository.findByUserId(userId)).thenReturn(List.of());

        assertFalse(checker.hasCompletedAnyMission(userIdStr));
    }

    @Test
    void parseUserId_throwsException_whenInvalidUUID() {
        assertThrows(IllegalArgumentException.class, () -> checker.hasCompletedAnyMission("invalid-uuid"));
    }
}