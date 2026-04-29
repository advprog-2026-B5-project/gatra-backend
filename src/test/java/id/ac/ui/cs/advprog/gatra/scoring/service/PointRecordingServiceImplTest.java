package id.ac.ui.cs.advprog.gatra.scoring.service;

import id.ac.ui.cs.advprog.gatra.scoring.model.PointActivityType;
import id.ac.ui.cs.advprog.gatra.scoring.model.PointHistory;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointRecordingServiceImplTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    private PointRecordingService pointRecordingService;

    @Captor
    private ArgumentCaptor<PointHistory> pointHistoryCaptor;

    @BeforeEach
    void setUp() {
        pointRecordingService = new PointRecordingServiceImpl(pointHistoryRepository);
    }

    @Test
    void testRecordPoints_SuccessfullySavesToRepository() {
        // Arrange
        String userId = "user-123";
        String clanId = "clan-456";
        double points = 150.0;
        PointActivityType activityType = PointActivityType.QUIZ_PASSED;
        String referenceId = "quiz-789";

        // Act
        pointRecordingService.recordPoints(userId, clanId, points, activityType, referenceId);

        // Assert
        // Capture the PointHistory object that was passed to the save() method
        verify(pointHistoryRepository).save(pointHistoryCaptor.capture());
        PointHistory savedRecord = pointHistoryCaptor.getValue();

        assertNotNull(savedRecord, "Saved record should not be null");
        assertEquals(userId, savedRecord.getUserId(), "User ID does not match");
        assertEquals(clanId, savedRecord.getClanId(), "Clan ID does not match");
        assertEquals(points, savedRecord.getPoints(), "Points do not match");
        assertEquals(activityType, savedRecord.getActivityType(), "Activity type does not match");
        assertEquals(referenceId, savedRecord.getReferenceId(), "Reference ID does not match");
    }
}