package id.ac.ui.cs.advprog.gatra.achievement.strategy;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowAchievementStrategyTest {

    @Mock
    private UserAchievementRepository repository;

    @InjectMocks
    private ShowAchievementStrategy strategy;

    private UserAchievement userAchievement;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");

        userAchievement = new UserAchievement();
        userAchievement.setUser(user);
        userAchievement.setDisplayed(false);
    }

    @Test
    void execute_setsDisplayedToTrueAndSaves_whenCountIsLessThanMax() {
        when(repository.countByUserUsernameAndIsDisplayedTrue("testuser")).thenReturn(2L);
        when(repository.save(userAchievement)).thenReturn(userAchievement);

        strategy.execute(userAchievement, repository);

        assertTrue(userAchievement.isDisplayed());
        verify(repository, times(1)).save(userAchievement);
    }

    @Test
    void execute_throwsException_whenCountIsMaxOrGreater() {
        when(repository.countByUserUsernameAndIsDisplayedTrue("testuser")).thenReturn(3L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            strategy.execute(userAchievement, repository);
        });

        assertEquals("Maksimal 3 achievement yang bisa ditampilkan di profil", exception.getMessage());
        assertFalse(userAchievement.isDisplayed());
        verify(repository, never()).save(any());
    }
}