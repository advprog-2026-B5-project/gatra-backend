package id.ac.ui.cs.advprog.gatra.clan.state;

import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;

public class RejectedState implements MembershipState{

    @Override
    public void approve(ClanMembership membership) {
        throw new IllegalStateException("Membership yang sudah ditolak tidak bisa diubah.");
    }

    @Override
    public void reject(ClanMembership membership) {
        throw new IllegalStateException("Membership sudah ditolak.");
    }
}
