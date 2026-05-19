package id.ac.ui.cs.advprog.gatra.clan.state;

import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RejectedStateTest {

    private RejectedState state;
    private ClanMembership membership;

    @BeforeEach
    void setUp() {
        state = new RejectedState();
        membership = new ClanMembership();
    }

    @Test
    void approve_throwsIllegalStateException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            state.approve(membership);
        });
        assertEquals("Membership yang sudah ditolak tidak bisa diubah.", exception.getMessage());
    }

    @Test
    void reject_throwsIllegalStateException() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            state.reject(membership);
        });
        assertEquals("Membership sudah ditolak.", exception.getMessage());
    }
}