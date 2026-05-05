package id.ac.ui.cs.advprog.gatra.achievement.controller;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MissionProgressResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.model.User;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.achievement.service.MissionProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionProgressController {

    private final MissionProgressService missionProgressService;
    private final UserRepository userRepository;

    @GetMapping("/progress")
    public ResponseEntity<List<MissionProgressResponse>> getActiveMissions(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = findUser(userDetails);
        return ResponseEntity.ok(
                missionProgressService.getActiveMissionsWithProgress(user.getId()));
    }

    @PostMapping("/{missionId}/claim")
    public ResponseEntity<MissionProgressResponse> claimReward(
            @PathVariable UUID missionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = findUser(userDetails);
        return ResponseEntity.ok(
                missionProgressService.claimReward(user.getId(), missionId));
    }

    private User findUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", userDetails.getUsername()));
    }
}