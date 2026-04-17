package id.ac.ui.cs.advprog.gatra.service.strategy;

import id.ac.ui.cs.advprog.gatra.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.repository.UserAchievementRepository;

public interface DisplayAchievementStrategy {
    void execute(UserAchievement userAchievement, UserAchievementRepository repository);
}