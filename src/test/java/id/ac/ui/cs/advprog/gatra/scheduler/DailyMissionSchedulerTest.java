package id.ac.ui.cs.advprog.gatra.scheduler;

import id.ac.ui.cs.advprog.gatra.service.DailyMissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyMissionSchedulerTest {

    @Mock
    private DailyMissionService dailyMissionService;

    @InjectMocks
    private DailyMissionScheduler dailyMissionScheduler;

    @Test
    void rotateDailyMissions_ShouldCallRotateMissions() {
        // Act
        dailyMissionScheduler.rotateDailyMissions();

        // Assert: Pastikan rotateMissions() dipanggil tepat 1 kali
        verify(dailyMissionService, times(1)).rotateMissions();
    }

    @Test
    void rotateDailyMissions_WhenExceptionThrown_ShouldNotCrash() {
        // Arrange: Simulasikan error dari service
        doThrow(new RuntimeException("Database down")).when(dailyMissionService).rotateMissions();

        // Act & Assert: Scheduler tidak boleh throw error ke atas (harus ditangkap oleh try-catch)
        dailyMissionScheduler.rotateDailyMissions();

        verify(dailyMissionService, times(1)).rotateMissions();
    }
}