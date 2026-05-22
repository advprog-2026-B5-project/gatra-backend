package id.ac.ui.cs.advprog.gatra.clan.event;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ClanReachedHighestTierEventTest {

    @Test
    void constructorAndGetters_success() {
        Object source = new Object();
        String expectedClanId = "clan-uuid-123";
        List<String> expectedMemberIds = List.of("user-uuid-1", "user-uuid-2", "user-uuid-3");

        ClanReachedHighestTierEvent event = new ClanReachedHighestTierEvent(source, expectedClanId, expectedMemberIds);

        assertEquals(source, event.getSource());
        assertEquals(expectedClanId, event.getClanId());
        assertEquals(expectedMemberIds, event.getMemberIds());
        assertEquals(3, event.getMemberIds().size());
    }

    @Test
    void constructorWithEmptyMemberIds_memberListEmpty() {
        Object source = new Object();
        ClanReachedHighestTierEvent event = new ClanReachedHighestTierEvent(source, "empty-clan", List.of());

        assertNotNull(event.getMemberIds());
        assertTrue(event.getMemberIds().isEmpty());
    }
}