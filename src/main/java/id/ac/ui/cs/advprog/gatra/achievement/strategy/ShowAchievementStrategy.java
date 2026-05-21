package id.ac.ui.cs.advprog.gatra.achievement.strategy;

import id.ac.ui.cs.advprog.gatra.achievement.model.UserAchievement;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowAchievementStrategy implements DisplayAchievementStrategy {

    private static final int MAX_DISPLAYED = 3;

    private final UserAchievementRepository userAchievementRepository;

    @Override
    public void execute(UserAchievement userAchievement) {

        long currentCount = userAchievementRepository.countByUserIdAndIsDisplayedTrue(
                userAchievement.getUserId()
        );

        if (currentCount >= MAX_DISPLAYED) {
            throw new IllegalStateException(
                    "Maksimal " + MAX_DISPLAYED + " achievement yang bisa ditampilkan di profil"
            );
        }

        userAchievement.setDisplayed(true);
        userAchievementRepository.save(userAchievement);
    }
}