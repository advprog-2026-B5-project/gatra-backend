package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.service.AchievementService;
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
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AchievementResponse> getAchievementById(@PathVariable UUID id) {
        return ResponseEntity.ok(achievementService.getAchievementById(id));
    }

    @PostMapping
    public ResponseEntity<AchievementResponse> createAchievement(
            @Valid @RequestBody AchievementRequest request) {
        return ResponseEntity.ok(achievementService.createAchievement(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AchievementResponse> updateAchievement(
            @PathVariable UUID id,
            @Valid @RequestBody AchievementRequest request) {
        return ResponseEntity.ok(achievementService.updateAchievement(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable UUID id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AchievementResponse>> getMyAchievements(java.security.Principal principal) {
        return ResponseEntity.ok(achievementService.getMyAchievements(principal.getName()));
    }

    @GetMapping("/me/displayed")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AchievementResponse>> getDisplayedAchievements(java.security.Principal principal) {
        return ResponseEntity.ok(achievementService.getDisplayedAchievements(principal.getName()));
    }

    @PatchMapping("/{id}/display")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> toggleDisplayAchievement(
            @PathVariable UUID id, @RequestParam boolean displayed,
            java.security.Principal principal)
    {
        achievementService.toggleDisplayAchievement(principal.getName(), id, displayed);
        return ResponseEntity.noContent().build();
    }
}