package id.ac.ui.cs.advprog.gatra.achievement.strategy;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowAchievementStrategyTest {

    @Mock
    private UserAchievementRepository repository;

    @InjectMocks
    private ShowAchievementStrategy strategy;

    private UserAchievement userAchievement;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userAchievement = new UserAchievement();
        userAchievement.setId(UUID.randomUUID());
        userAchievement.setUserId(userId); // Use the new soft reference!
        userAchievement.setDisplayed(false);
    }

    @Test
    void execute_ShouldSetDisplayedTrue_WhenCountIsLessThanMax() {
        // Mock the new ByUserId method
        when(repository.countByUserIdAndIsDisplayedTrue(userId)).thenReturn(2L);

        strategy.execute(userAchievement, repository);

        assertTrue(userAchievement.isDisplayed());
        verify(repository, times(1)).save(userAchievement);
    }

    @Test
    void execute_ShouldThrowException_WhenCountIsMax() {
        when(repository.countByUserIdAndIsDisplayedTrue(userId)).thenReturn(3L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            strategy.execute(userAchievement, repository);
        });

        assertEquals("Maksimal 3 achievement yang bisa ditampilkan di profil", exception.getMessage());
        verify(repository, never()).save(any());
    }
}