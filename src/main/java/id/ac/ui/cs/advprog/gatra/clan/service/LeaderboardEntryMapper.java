package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeaderboardEntryMapper {

    private final BuffDebuffService buffDebuffService;

    public LeaderboardEntryResponse toEntry(Clan clan) {
        double score = calculateScore(clan);
        return LeaderboardEntryResponse.builder()
                .clanId(clan.getId())
                .clanName(clan.getName())
                .tier(clan.getTier())
                .score(score)
                .build();
    }

    private double calculateScore(Clan clan) {
        return buffDebuffService.buildCalculator(clan.getId())
                .calculate(clan.getId(), clan.getTier());
    }
}
