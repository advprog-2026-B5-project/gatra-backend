package id.ac.ui.cs.advprog.gatra.clan.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TierLeaderboardResponse {
    private String tier;
    private List<LeaderboardEntryResponse> rankings;
}