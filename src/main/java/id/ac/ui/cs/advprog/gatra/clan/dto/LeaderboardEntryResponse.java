package id.ac.ui.cs.advprog.gatra.clan.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class LeaderboardEntryResponse {
    private int rank;
    private String clanId;
    private String clanName;
    private String tier;
    private double score;
}