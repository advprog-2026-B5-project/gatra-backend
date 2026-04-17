package id.ac.ui.cs.advprog.gatra.scheduler;

import id.ac.ui.cs.advprog.gatra.service.DailyMissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyMissionScheduler {

    private final DailyMissionService dailyMissionService;

    // Menjalankan cron job setiap hari pada pukul 00:00:00 Waktu Jakarta (WIB)
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Jakarta")
    public void rotateDailyMissions() {
        log.info("Memulai proses rotasi otomatis Daily Mission...");
        try {
            dailyMissionService.rotateMissions();
            log.info("Rotasi Daily Mission berhasil. Misi harian telah diperbarui.");
        } catch (Exception e) {
            log.error("Terjadi kesalahan saat merotasi Daily Mission: {}", e.getMessage());
        }
    }
}