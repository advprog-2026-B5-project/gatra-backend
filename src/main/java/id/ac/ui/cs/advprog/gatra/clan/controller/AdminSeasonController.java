package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.SeasonResultResponse;
import id.ac.ui.cs.advprog.gatra.clan.service.ClanSeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/season")
public class AdminSeasonController {

    @Autowired
    private ClanSeasonService clanSeasonService;

    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetSeason() {
        try {
            SeasonResultResponse result = clanSeasonService.endSeason();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to reset season: " + e.getMessage()));
        }
    }
}