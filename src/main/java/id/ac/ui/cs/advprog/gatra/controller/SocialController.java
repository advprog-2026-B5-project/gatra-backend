package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.auth.dto.PublicProfileResponse;
import id.ac.ui.cs.advprog.gatra.auth.dto.UserSearchResponse;
import id.ac.ui.cs.advprog.gatra.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController {

    private final SocialService socialService;

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(@RequestParam("q") String query) {
        return ResponseEntity.ok(socialService.searchUsers(query));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(@PathVariable String username) {
        return ResponseEntity.ok(socialService.getPublicProfile(username));
    }
}