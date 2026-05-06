package id.ac.ui.cs.advprog.gatra.achievement.controller;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.achievement.service.MissionProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentMissionController {

    private final MissionProgressService missionProgressService;

    @GetMapping("/me")
    public ResponseEntity<List<MissionProgressResponse>> getMyMissions(
        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(missionProgressService.getActiveMissionsWithProgress(userDetails.getUsername()));
    }

    @PostMapping("/me/claim/{missionId}")
    public ResponseEntity<MissionProgressResponse> claimReward(
            @PathVariable UUID missionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                missionProgressService.claimReward(userDetails.getUsername(), missionId));
    }
}