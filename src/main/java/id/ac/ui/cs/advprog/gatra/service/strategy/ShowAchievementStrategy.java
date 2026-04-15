package id.ac.ui.cs.advprog.gatra.service.strategy;

import id.ac.ui.cs.advprog.gatra.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.repository.UserAchievementRepository;
import org.springframework.stereotype.Component;

@Component
public class ShowAchievementStrategy implements DisplayAchievementStrategy {

    private static final int MAX_DISPLAYED = 3;

    @Override
    public void execute(UserAchievement userAchievement, UserAchievementRepository repository) {
        long currentCount = repository.countShownAchievements(
                userAchievement.getUser().getUsername()
        );

        if (currentCount >= MAX_DISPLAYED) {
            throw new IllegalStateException(
                    "Maksimal " + MAX_DISPLAYED + " achievement yang bisa ditampilkan di profil"
            );
        }
        userAchievement.setDisplayed(true);
        repository.save(userAchievement);
    }
}