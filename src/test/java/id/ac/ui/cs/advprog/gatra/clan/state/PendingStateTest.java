package id.ac.ui.cs.advprog.gatra.clan.state;

import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PendingStateTest {

    private PendingState state;
    private ClanMembership membership;

    @BeforeEach
    void setUp() {
        state = new PendingState();
        membership = new ClanMembership();
        membership.setStatus(MembershipStatus.PENDING);
        membership.setState(state);
    }

    @Test
    void approve_changesStatusToApproved_andStateToApprovedState() {
        state.approve(membership);
        assertEquals(MembershipStatus.APPROVED, membership.getStatus());
        assertTrue(membership.getState() instanceof ApprovedState);
    }

    @Test
    void reject_changesStatusToRejected_andStateToRejectedState() {
        state.reject(membership);
        assertEquals(MembershipStatus.REJECTED, membership.getStatus());
        assertTrue(membership.getState() instanceof RejectedState);
    }
}