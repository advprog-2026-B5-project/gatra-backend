package id.ac.ui.cs.advprog.gatra.clan.state;

import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;

public interface MembershipState {
    void approve(ClanMembership membership);
    void reject(ClanMembership membership);
}