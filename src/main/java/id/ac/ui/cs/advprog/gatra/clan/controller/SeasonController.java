package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.SeasonResultResponse;
import id.ac.ui.cs.advprog.gatra.clan.service.ClanSeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clans/season")
@RequiredArgsConstructor
public class SeasonController {

    private final ClanSeasonService seasonService;

    @GetMapping("/last")
    public ResponseEntity<SeasonResultResponse> getLastSeason() {
        return ResponseEntity.ok(seasonService.getLastSeasonResult());
    }

    @PostMapping("/end")
    public ResponseEntity<SeasonResultResponse> endSeason() {
        return ResponseEntity.ok(seasonService.endSeason());
    }
}