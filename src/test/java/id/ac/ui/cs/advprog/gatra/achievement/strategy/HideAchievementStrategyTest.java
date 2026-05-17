package id.ac.ui.cs.advprog.gatra.achievement.strategy;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HideAchievementStrategyTest {

    @Mock
    private UserAchievementRepository repository;

    @InjectMocks
    private HideAchievementStrategy strategy;

    private UserAchievement userAchievement;

    @BeforeEach
    void setUp() {
        userAchievement = new UserAchievement();
        userAchievement.setDisplayed(true);
    }

    @Test
    void execute_setsDisplayedToFalseAndSaves() {
        when(repository.save(userAchievement)).thenReturn(userAchievement);

        strategy.execute(userAchievement, repository);

        assertFalse(userAchievement.isDisplayed());
        verify(repository, times(1)).save(userAchievement);
    }
}