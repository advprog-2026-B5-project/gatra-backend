package id.ac.ui.cs.advprog.gatra.service.strategy;

import id.ac.ui.cs.advprog.gatra.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.repository.UserAchievementRepository;

public class HideAchievementStrategy implements DisplayAchievementStrategy {

    @Override
    public void execute(UserAchievement userAchievement, UserAchievementRepository repository) {
        userAchievement.setDisplayed(false);
        repository.save(userAchievement);
    }
}