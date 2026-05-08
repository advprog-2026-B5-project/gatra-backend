package id.ac.ui.cs.advprog.gatra.achievement.seed;

import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.AchievementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementSeederTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private ApplicationArguments args;

    @InjectMocks
    private AchievementSeeder achievementSeeder;

    @Test
    void testRun_SeedsAchievementWhenNotExists() {
        when(achievementRepository.existsByName("Diamond Clan")).thenReturn(false);

        achievementSeeder.run(args);

        verify(achievementRepository, times(1)).existsByName("Diamond Clan");
        verify(achievementRepository, times(1)).save(any(Achievement.class));
    }

    @Test
    void testRun_DoesNotSeedWhenAchievementExists() {
        when(achievementRepository.existsByName("Diamond Clan")).thenReturn(true);

        achievementSeeder.run(args);

        verify(achievementRepository, times(1)).existsByName("Diamond Clan");
        verify(achievementRepository, never()).save(any(Achievement.class));
    }
}