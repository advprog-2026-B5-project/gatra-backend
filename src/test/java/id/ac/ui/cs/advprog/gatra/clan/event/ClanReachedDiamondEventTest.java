package id.ac.ui.cs.advprog.gatra.clan.event;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ClanReachedDiamondEventTest {

    @Test
    void testClanReachedDiamondEventConstructorAndGetters() {
        Object source = new Object();
        String expectedClanId = "clan-uuid-123";
        List<String> expectedMemberIds = List.of(
                "user-uuid-1",
                "user-uuid-2",
                "user-uuid-3"
        );

        ClanReachedDiamondEvent event = new ClanReachedDiamondEvent(source, expectedClanId, expectedMemberIds);

        assertEquals(source, event.getSource(), "Source event harus sama dengan yang diinputkan");
        assertEquals(expectedClanId, event.getClanId(), "Clan ID harus sesuai dengan yang diinputkan");
        assertEquals(expectedMemberIds, event.getMemberIds(), "List member IDs harus sesuai dengan yang diinputkan");
        assertEquals(3, event.getMemberIds().size(), "Jumlah member IDs harus tepat");
    }

    @Test
    void testClanReachedDiamondEventWithEmptyList() {
        Object source = new Object();
        String clanId = "empty-clan";
        List<String> emptyMembers = List.of();

        ClanReachedDiamondEvent event = new ClanReachedDiamondEvent(source, clanId, emptyMembers);

        assertNotNull(event.getMemberIds(), "List member tidak boleh null");
        assertTrue(event.getMemberIds().isEmpty(), "List member harus kosong");
    }
}