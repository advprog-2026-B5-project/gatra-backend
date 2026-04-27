package id.ac.ui.cs.advprog.gatra.clan.state;

import id.ac.ui.cs.advprog.gatra.model.ClanMembership;

public class ApprovedState implements MembershipState {

    @Override
    public void approve(ClanMembership membership) {
        throw new IllegalStateException("Membership sudah disetujui.");
    }

    @Override
    public void reject(ClanMembership membership) {
        throw new IllegalStateException("Membership yang sudah disetujui tidak bisa ditolak.");
    }
}