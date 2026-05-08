package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clans/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<TierLeaderboardResponse>> getAllTierLeaderboards() {
        return ResponseEntity.ok(leaderboardService.getAllTierLeaderboards());
    }

    @GetMapping("/{tier}")
    public ResponseEntity<TierLeaderboardResponse> getLeaderboardByTier(
            @PathVariable String tier) {
        return ResponseEntity.ok(leaderboardService.getLeaderboardByTier(tier));
    }
}