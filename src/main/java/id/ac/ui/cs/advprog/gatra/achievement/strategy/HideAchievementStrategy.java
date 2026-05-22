package id.ac.ui.cs.advprog.gatra.achievement.strategy;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HideAchievementStrategy implements DisplayAchievementStrategy {

    private final UserAchievementRepository userAchievementRepository;

    @Override
    public void execute(UserAchievement userAchievement) {
        userAchievement.setDisplayed(false);
        userAchievementRepository.save(userAchievement);
    }
}