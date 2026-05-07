package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.scoring.service.SeasonService;
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
    private SeasonService seasonService;

    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')") // hanya admin yang bisa mengakses
    public ResponseEntity<?> resetSeason() {
        try {
            seasonService.resetSeason();
            return ResponseEntity.ok(Map.of("message", "Season reset successfully. All leaderboards are now 0."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to reset season: " + e.getMessage()));
        }
    }
}