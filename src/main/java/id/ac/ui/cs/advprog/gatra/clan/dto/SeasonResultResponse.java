package id.ac.ui.cs.advprog.gatra.clan.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class SeasonResultResponse {
    private int seasonNumber;
    private LocalDateTime frozenAt;
    private List<TierLeaderboardResponse> leaderboards;
}