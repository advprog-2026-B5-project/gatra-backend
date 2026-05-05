package id.ac.ui.cs.advprog.gatra.achievement.controller;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.service.AchievementService;
import id.ac.ui.cs.advprog.gatra.achievement.service.UserAchievementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class StudentAchievementController {

    private final UserAchievementService userAchievementService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AchievementResponse>> getMyAchievements(java.security.Principal principal) {
        return ResponseEntity.ok(userAchievementService.getMyAchievements(principal.getName()));
    }

    @GetMapping("/me/displayed")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AchievementResponse>> getDisplayedAchievements(java.security.Principal principal) {
        return ResponseEntity.ok(userAchievementService.getDisplayedAchievements(principal.getName()));
    }

    @PatchMapping("/{id}/display")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> toggleDisplayAchievement(
            @PathVariable UUID id, @RequestParam boolean displayed,
            java.security.Principal principal)
    {
        userAchievementService.toggleDisplayAchievement(principal.getName(), id, displayed);
        return ResponseEntity.noContent().build();
    }
}