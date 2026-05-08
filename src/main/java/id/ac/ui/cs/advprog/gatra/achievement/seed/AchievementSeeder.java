package id.ac.ui.cs.advprog.gatra.achievement.seed;

import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AchievementSeeder implements ApplicationRunner {

    private final AchievementRepository achievementRepository;

    @Override
    public void run(ApplicationArguments args) {
        seedDiamondClanAchievement();
    }

    private void seedDiamondClanAchievement() {
        if (achievementRepository.existsByName("Diamond Clan")) {
            log.info("Achievement 'Diamond Clan' already exists");
            return;
        }

        achievementRepository.save(Achievement.builder()
                .name("Diamond Clan")
                .category(ActionType.HIGHEST_TIER)
                .milestoneThreshold(1)
                .description("Mencapai tier diamond bersama clan")
                .badgeUrl("https://thumbnail.imgbin.com/18/19/14/imgbin-clash-of-clans-clan-badge-video-gaming-clan-clash-royale-clash-of-clans-uGznzWA6FbDyWRCCzSuw0fU4g_t.jpg")
                .build());
        log.info("Seeded achievement: Diamond Clan");
    }
}