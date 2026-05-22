package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.achievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserMissionProgressRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AchievementMetricsService {

    private final MeterRegistry meterRegistry;
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final UserMissionProgressRepository userMissionProgressRepository;

    @PostConstruct
    public void registerMetrics() {

        Gauge.builder("gatra.achievement.total",
                        achievementRepository, repo -> (double) repo.count())
                .description("Total achievement yang terdaftar di sistem")
                .tag("module", "achievement")
                .register(meterRegistry);

        Gauge.builder("gatra.achievement.unlocked.total",
                        userAchievementRepository, repo -> (double) repo.count())
                .description("Total entri UserAchievement (user x achievement yang di-unlock)")
                .tag("module", "achievement")
                .register(meterRegistry);

        Gauge.builder("gatra.achievement.displayed.total",
                        userAchievementRepository,
                        repo -> (double) repo.countByIsDisplayedTrue())
                .description("Total achievement yang sedang ditampilkan oleh semua user")
                .tag("module", "achievement")
                .register(meterRegistry);

        Gauge.builder("gatra.mission.total",
                        dailyMissionRepository, repo -> (double) repo.count())
                .description("Total daily mission yang terdaftar di sistem")
                .tag("module", "achievement")
                .register(meterRegistry);

        Gauge.builder("gatra.mission.active",
                        dailyMissionRepository,
                        repo -> (double) repo.findByStatus(MissionStatus.ACTIVE).size())
                .description("Jumlah daily mission yang aktif saat ini")
                .tag("module", "achievement")
                .register(meterRegistry);

        Gauge.builder("gatra.mission.progress.total",
                        userMissionProgressRepository, repo -> (double) repo.count())
                .description("Total record progres misi dari semua user")
                .tag("module", "achievement")
                .register(meterRegistry);

        Gauge.builder("gatra.mission.progress.claimed",
                        userMissionProgressRepository,
                        repo -> (double) repo.countByIsClaimedTrue())
                .description("Total progres misi yang reward-nya sudah diklaim")
                .tag("module", "achievement")
                .register(meterRegistry);
    }
}