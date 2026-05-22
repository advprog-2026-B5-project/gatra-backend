package id.ac.ui.cs.advprog.gatra.clan.event;

import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class SeasonEndedEvent {
    private final List<TierLeaderboardResponse> leaderboards;

    public SeasonEndedEvent(List<TierLeaderboardResponse> leaderboards) {
        this.leaderboards = leaderboards;
    }
}