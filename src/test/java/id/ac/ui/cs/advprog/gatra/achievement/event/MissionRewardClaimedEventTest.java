package id.ac.ui.cs.advprog.gatra.achievement.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MissionRewardClaimedEventTest {

    @Test
    void shouldCreateEventWithCorrectValues() {
        UUID userId = UUID.randomUUID();
        UUID missionId = UUID.randomUUID();
        int rewardPoints = 100;

        MissionRewardClaimedEvent event = new MissionRewardClaimedEvent(userId, missionId, rewardPoints);

        assertEquals(userId, event.userId());
        assertEquals(missionId, event.missionId());
        assertEquals(rewardPoints, event.rewardPoints());
    }

    @Test
    void shouldSupportZeroRewardPoints() {
        UUID userId = UUID.randomUUID();
        UUID missionId = UUID.randomUUID();

        MissionRewardClaimedEvent event = new MissionRewardClaimedEvent(userId, missionId, 0);

        assertEquals(0, event.rewardPoints());
    }

    @Test
    void twoEventsWithSameValues_shouldBeEqual() {
        UUID userId = UUID.randomUUID();
        UUID missionId = UUID.randomUUID();

        MissionRewardClaimedEvent event1 = new MissionRewardClaimedEvent(userId, missionId, 50);
        MissionRewardClaimedEvent event2 = new MissionRewardClaimedEvent(userId, missionId, 50);

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void twoEventsWithDifferentValues_shouldNotBeEqual() {
        UUID userId = UUID.randomUUID();
        UUID missionId = UUID.randomUUID();

        MissionRewardClaimedEvent event1 = new MissionRewardClaimedEvent(userId, missionId, 50);
        MissionRewardClaimedEvent event2 = new MissionRewardClaimedEvent(userId, missionId, 100);

        assertNotEquals(event1, event2);
    }

    @Test
    void toString_shouldContainAllFields() {
        UUID userId = UUID.randomUUID();
        UUID missionId = UUID.randomUUID();

        MissionRewardClaimedEvent event = new MissionRewardClaimedEvent(userId, missionId, 75);
        String str = event.toString();

        assertTrue(str.contains(userId.toString()));
        assertTrue(str.contains(missionId.toString()));
        assertTrue(str.contains("75"));
    }
}
