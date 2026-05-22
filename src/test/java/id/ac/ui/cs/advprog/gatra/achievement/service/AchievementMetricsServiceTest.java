package id.ac.ui.cs.advprog.gatra.achievement.service;

import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.achievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserMissionProgressRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AchievementMetricsServiceTest {

    private MeterRegistry meterRegistry;

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private DailyMissionRepository dailyMissionRepository;

    @Mock
    private UserMissionProgressRepository userMissionProgressRepository;

    private AchievementMetricsService metricsService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        when(achievementRepository.count()).thenReturn(5L);
        when(userAchievementRepository.count()).thenReturn(12L);
        when(userAchievementRepository.countByIsDisplayedTrue()).thenReturn(8L);
        when(dailyMissionRepository.count()).thenReturn(6L);
        when(dailyMissionRepository.findByStatus(MissionStatus.ACTIVE))
                .thenReturn(List.of(
                        new DailyMission(), new DailyMission(), new DailyMission()
                ));
        when(userMissionProgressRepository.count()).thenReturn(20L);
        when(userMissionProgressRepository.countByIsClaimedTrue()).thenReturn(7L);

        metricsService = new AchievementMetricsService(
                meterRegistry,
                achievementRepository,
                userAchievementRepository,
                dailyMissionRepository,
                userMissionProgressRepository
        );
        metricsService.registerMetrics();
    }

    @Test
    void registerMetrics_shouldRegisterAchievementTotalGauge() {
        Gauge gauge = meterRegistry.find("gatra.achievement.total").gauge();
        assertNotNull(gauge, "Gauge gatra.achievement.total seharusnya terdaftar");
        assertEquals(5.0, gauge.value());
    }

    @Test
    void registerMetrics_shouldRegisterAchievementUnlockedTotalGauge() {
        Gauge gauge = meterRegistry.find("gatra.achievement.unlocked.total").gauge();
        assertNotNull(gauge, "Gauge gatra.achievement.unlocked.total seharusnya terdaftar");
        assertEquals(12.0, gauge.value());
    }

    @Test
    void registerMetrics_shouldRegisterAchievementDisplayedTotalGauge() {
        Gauge gauge = meterRegistry.find("gatra.achievement.displayed.total").gauge();
        assertNotNull(gauge, "Gauge gatra.achievement.displayed.total seharusnya terdaftar");
        assertEquals(8.0, gauge.value());
    }

    @Test
    void registerMetrics_shouldRegisterMissionTotalGauge() {
        Gauge gauge = meterRegistry.find("gatra.mission.total").gauge();
        assertNotNull(gauge, "Gauge gatra.mission.total seharusnya terdaftar");
        assertEquals(6.0, gauge.value());
    }

    @Test
    void registerMetrics_shouldRegisterMissionActiveGauge() {
        Gauge gauge = meterRegistry.find("gatra.mission.active").gauge();
        assertNotNull(gauge, "Gauge gatra.mission.active seharusnya terdaftar");
        assertEquals(3.0, gauge.value());
    }

    @Test
    void registerMetrics_shouldRegisterMissionProgressTotalGauge() {
        Gauge gauge = meterRegistry.find("gatra.mission.progress.total").gauge();
        assertNotNull(gauge, "Gauge gatra.mission.progress.total seharusnya terdaftar");
        assertEquals(20.0, gauge.value());
    }

    @Test
    void registerMetrics_shouldRegisterMissionProgressClaimedGauge() {
        Gauge gauge = meterRegistry.find("gatra.mission.progress.claimed").gauge();
        assertNotNull(gauge, "Gauge gatra.mission.progress.claimed seharusnya terdaftar");
        assertEquals(7.0, gauge.value());
    }

    @Test
    void registerMetrics_allGaugesHaveModuleTag() {
        meterRegistry.getMeters().stream()
                .filter(m -> m.getId().getName().startsWith("gatra."))
                .forEach(m -> assertEquals(
                        "achievement",
                        m.getId().getTag("module"),
                        "Semua gauge achievement harus memiliki tag module=achievement"
                ));
    }
}