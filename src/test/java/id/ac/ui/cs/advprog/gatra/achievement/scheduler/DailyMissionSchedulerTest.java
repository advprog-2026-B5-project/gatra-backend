package id.ac.ui.cs.advprog.gatra.achievement.scheduler;

import id.ac.ui.cs.advprog.gatra.achievement.service.DailyMissionService;
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
        dailyMissionScheduler.rotateDailyMissions();

        verify(dailyMissionService, times(1)).rotateMissions();
    }

    @Test
    void rotateDailyMissions_WhenExceptionThrown_ShouldNotCrash() {
        doThrow(new RuntimeException("Database down")).when(dailyMissionService).rotateMissions();

        dailyMissionScheduler.rotateDailyMissions();

        verify(dailyMissionService, times(1)).rotateMissions();
    }
}