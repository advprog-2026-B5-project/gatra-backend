package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.SeasonResultResponse;
import id.ac.ui.cs.advprog.gatra.clan.service.ClanSeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/season")

@RequiredArgsConstructor
public class AdminSeasonController {

    private final ClanSeasonService clanSeasonService;

    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeasonResultResponse> resetSeason() {
        SeasonResultResponse result = clanSeasonService.endSeason();
        return ResponseEntity.ok(result);

    }
}