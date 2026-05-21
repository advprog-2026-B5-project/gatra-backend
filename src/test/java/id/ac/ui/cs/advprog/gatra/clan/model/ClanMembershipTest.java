package id.ac.ui.cs.advprog.gatra.clan.model;

import id.ac.ui.cs.advprog.gatra.clan.state.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClanMembershipTest {

    @Test
    void initState_pending() {
        ClanMembership m = new ClanMembership();
        m.setStatus(MembershipStatus.PENDING);
        m.initState();
        assertTrue(m.getState() instanceof PendingState);
    }

    @Test
    void initState_approved() {
        ClanMembership m = new ClanMembership();
        m.setStatus(MembershipStatus.APPROVED);
        m.initState();
        assertTrue(m.getState() instanceof ApprovedState);
    }

    @Test
    void initState_rejected() {
        ClanMembership m = new ClanMembership();
        m.setStatus(MembershipStatus.REJECTED);
        m.initState();
        assertTrue(m.getState() instanceof RejectedState);
    }
}