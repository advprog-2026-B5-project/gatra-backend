package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import java.util.List;

public interface ClanMembershipService {
    MembershipResponse applyToClan(String clanId, String userId);
    MembershipResponse decideMembership(String clanId, String applicantId,
                                        MembershipDecisionRequest request, String leaderId);
    List<MembershipResponse> getPendingApplications(String clanId, String leaderId);
    void leaveClan(String clanId, String userId);
}
