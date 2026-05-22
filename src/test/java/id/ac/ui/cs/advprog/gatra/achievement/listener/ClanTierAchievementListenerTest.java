package id.ac.ui.cs.advprog.gatra.achievement.listener;

import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.achievement.service.UserAchievementService;
import id.ac.ui.cs.advprog.gatra.clan.event.ClanReachedHighestTierEvent;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanTierAchievementListenerTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementService userAchievementService;

    @InjectMocks
    private ClanTierAchievementListener listener;

    private Achievement diamondAchievement;
    private String clanId;
    private List<String> memberIds;

    @BeforeEach
    void setUp() {
        clanId = "clan-123";
        memberIds = List.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        diamondAchievement = Achievement.builder()
                .id(UUID.randomUUID())
                .name("Diamond Clan")
                .build();
    }

    @Test
    void testOnClanReachedDiamond_Success() {
        ClanReachedHighestTierEvent event = new ClanReachedHighestTierEvent(this, clanId, memberIds);
        when(achievementRepository.findByName("Diamond Clan")).thenReturn(Optional.of(diamondAchievement));

        listener.onClanReachedHighestTier(event);

        verify(achievementRepository, times(1)).findByName("Diamond Clan");
        verify(userAchievementService, times(memberIds.size())).unlockIfNotYet(any(UUID.class), eq(diamondAchievement));

        for (String id : memberIds) {
            verify(userAchievementService).unlockIfNotYet(UUID.fromString(id), diamondAchievement);
        }
    }

    @Test
    void testOnClanReachedDiamond_AchievementNotFound_ThrowsException() {
        ClanReachedHighestTierEvent event = new ClanReachedHighestTierEvent(this, clanId, memberIds);
        when(achievementRepository.findByName("Diamond Clan")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            listener.onClanReachedHighestTier(event);
        });

        verify(userAchievementService, never()).unlockIfNotYet(any(), any());
    }

    @Test
    void testOnClanReachedDiamond_EmptyMemberIds_DoesNothing() {
        ClanReachedHighestTierEvent event = new ClanReachedHighestTierEvent(this, clanId, List.of());
        when(achievementRepository.findByName("Diamond Clan")).thenReturn(Optional.of(diamondAchievement));

        listener.onClanReachedHighestTier(event);

        verify(achievementRepository, times(1)).findByName("Diamond Clan");
        verify(userAchievementService, never()).unlockIfNotYet(any(), any());
    }

    @Test
    void testOnClanReachedDiamond_InvalidUuidFormat_ThrowsException() {
        List<String> invalidIds = List.of("uuid-tidak-valid");
        ClanReachedHighestTierEvent event = new ClanReachedHighestTierEvent(this, clanId, invalidIds);
        when(achievementRepository.findByName("Diamond Clan")).thenReturn(Optional.of(diamondAchievement));

        assertThrows(IllegalArgumentException.class, () -> {
            listener.onClanReachedHighestTier(event);
        });
    }
}