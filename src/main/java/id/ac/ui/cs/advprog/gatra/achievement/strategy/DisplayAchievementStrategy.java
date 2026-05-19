package id.ac.ui.cs.advprog.gatra.achievement.strategy;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;

public interface DisplayAchievementStrategy {
    void execute(UserAchievement userAchievement, UserAchievementRepository repository);
}