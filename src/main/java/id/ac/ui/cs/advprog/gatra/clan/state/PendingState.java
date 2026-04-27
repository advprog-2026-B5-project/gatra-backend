package id.ac.ui.cs.advprog.gatra.clan.state;


import id.ac.ui.cs.advprog.gatra.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;

public class PendingState implements MembershipState {

    @Override
    public void approve(ClanMembership membership) {
        membership.setStatus(MembershipStatus.APPROVED);
        membership.setState(new ApprovedState());
    }

    @Override
    public void reject(ClanMembership membership) {
        membership.setStatus(MembershipStatus.REJECTED);
        membership.setState(new RejectedState());
    }
}
