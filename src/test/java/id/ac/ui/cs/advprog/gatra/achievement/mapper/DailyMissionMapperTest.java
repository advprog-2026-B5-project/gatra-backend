package id.ac.ui.cs.advprog.gatra.achievement.mapper;

import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.gatra.achievement.model.MissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DailyMissionMapperTest {

    private DailyMissionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DailyMissionMapper();
    }

    @Test
    void toResponse_null() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void toResponse_success() {
        DailyMission mission = DailyMission.builder()
                .id(UUID.randomUUID())
                .title("T")
                .actionType(ActionType.FINISH_QUIZ)
                .status(MissionStatus.ACTIVE)
                .build();
        DailyMissionResponse res = mapper.toResponse(mission);
        assertNotNull(res);
        assertEquals("FINISH_QUIZ", res.getActionType());
        assertEquals("ACTIVE", res.getStatus());
    }

    @Test
    void toEntity_null() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toEntity_success() {
        DailyMissionRequest req = new DailyMissionRequest();
        req.setTitle("T");
        req.setActionType("FINISH_QUIZ");
        req.setStatus("ACTIVE");

        DailyMission res = mapper.toEntity(req);
        assertNotNull(res);
        assertEquals(ActionType.FINISH_QUIZ, res.getActionType());
        assertEquals(MissionStatus.ACTIVE, res.getStatus());
    }

    @Test
    void updateEntityFromRequest_nulls() {
        assertDoesNotThrow(() -> mapper.updateEntityFromRequest(null, new DailyMission()));
        assertDoesNotThrow(() -> mapper.updateEntityFromRequest(new DailyMissionRequest(), null));
    }

    @Test
    void updateEntityFromRequest_success() {
        DailyMission mission = new DailyMission();
        DailyMissionRequest req = new DailyMissionRequest();
        req.setTitle("Updated");
        req.setActionType("FINISH_QUIZ");
        req.setStatus("INACTIVE");

        mapper.updateEntityFromRequest(req, mission);

        assertEquals("Updated", mission.getTitle());
        assertEquals(ActionType.FINISH_QUIZ, mission.getActionType());
        assertEquals(MissionStatus.INACTIVE, mission.getStatus());
    }
}