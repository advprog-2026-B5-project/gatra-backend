package id.ac.ui.cs.advprog.gatra.clan.dto;

import id.ac.ui.cs.advprog.gatra.clan.model.ClanRole;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MembershipResponse {
    private String id;
    private String clanId;
    private String userId;
    private ClanRole role;
    private MembershipStatus status;
}
