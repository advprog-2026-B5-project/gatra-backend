package id.ac.ui.cs.advprog.gatra.achievement.controller;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.service.UserAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentAchievementController {

    private final UserAchievementService userAchievementService;

    @GetMapping("/me")
    public ResponseEntity<List<AchievementResponse>> getMyAchievements(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userAchievementService.getMyAchievements(userDetails.getUsername()));
    }

    @GetMapping("/me/displayed")
    public ResponseEntity<List<AchievementResponse>> getDisplayedAchievements(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userAchievementService.getDisplayedAchievements(userDetails.getUsername()));
    }

    @PatchMapping("/{id}/display")
    public ResponseEntity<Void> toggleDisplayAchievement(
            @PathVariable UUID id,
            @RequestParam boolean displayed,
            @AuthenticationPrincipal UserDetails userDetails) {
        userAchievementService.toggleDisplayAchievement(userDetails.getUsername(), id, displayed);
        return ResponseEntity.noContent().build();
    }
}