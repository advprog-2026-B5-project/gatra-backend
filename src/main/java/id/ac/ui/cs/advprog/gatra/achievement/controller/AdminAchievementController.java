package id.ac.ui.cs.advprog.gatra.achievement.controller;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.achievement.service.AchievementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/achievements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAchievementController {

    private final AchievementService achievementService;

    @PostMapping
    public ResponseEntity<AchievementResponse> createAchievement(
            @Valid @RequestBody AchievementRequest request) {
        return ResponseEntity.ok(achievementService.createAchievement(request));
    }

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AchievementResponse> getAchievementById(@PathVariable UUID id) {
        return ResponseEntity.ok(achievementService.getAchievementById(id));
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
}