package id.ac.ui.cs.advprog.gatra.clan.state;

import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApprovedStateTest {

    private ApprovedState state;
    private ClanMembership membership;

    @BeforeEach
    void setUp() {
        state = new ApprovedState();
        membership = new ClanMembership();
    }

    @Test
    void approve_throwsIllegalStateException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            state.approve(membership);
        });
        assertEquals("Membership sudah disetujui.", exception.getMessage());
    }

    @Test
    void reject_throwsIllegalStateException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            state.reject(membership);
        });
        assertEquals("Membership yang sudah disetujui tidak bisa ditolak.", exception.getMessage());
    }
}