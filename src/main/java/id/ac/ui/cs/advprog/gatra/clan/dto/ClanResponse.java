package id.ac.ui.cs.advprog.gatra.clan.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class ClanResponse {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    private String myRole;
    private String membershipStatus;
    private List<MembershipResponse> pendingApplications;

    private Integer memberCount;
    private List<MembershipResponse> members;

    private Double score;
    private String tier;
}
