package id.ac.ui.cs.advprog.gatra.achievement.mapper;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;
import id.ac.ui.cs.advprog.gatra.achievement.model.UserMissionProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MissionProgressMapperTest {

    private MissionProgressMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MissionProgressMapper();
    }

    @Test
    void toResponse_withNullProgress() {
        DailyMission mission = DailyMission.builder()
                .id(UUID.randomUUID())
                .title("M1")
                .targetCount(5)
                .actionType(ActionType.FINISH_QUIZ)
                .status(MissionStatus.ACTIVE)
                .build();
        
        MissionProgressResponse res = mapper.toResponse(mission, null);
        
        assertNotNull(res);
        assertEquals(0, res.getCurrentCount());
        assertFalse(res.getIsClaimed());
        assertFalse(res.getIsCompleted());
        assertEquals("FINISH_QUIZ", res.getActionType());
        assertEquals("ACTIVE", res.getStatus());
    }

    @Test
    void toResponse_withProgress() {
        DailyMission mission = DailyMission.builder()
                .id(UUID.randomUUID())
                .title("M1")
                .targetCount(5)
                .build();
        UserMissionProgress progress = UserMissionProgress.builder()
                .currentCount(5)
                .isClaimed(true)
                .build();
        
        MissionProgressResponse res = mapper.toResponse(mission, progress);
        
        assertNotNull(res);
        assertEquals(5, res.getCurrentCount());
        assertTrue(res.getIsClaimed());
        assertTrue(res.getIsCompleted());
    }

    @Test
    void toResponse_withNullEnumsInMission() {
        DailyMission mission = DailyMission.builder()
                .id(UUID.randomUUID())
                .title("M1")
                .targetCount(5)
                .build();
        
        MissionProgressResponse res = mapper.toResponse(mission, null);
        assertNull(res.getActionType());
        assertNull(res.getStatus());
    }
}