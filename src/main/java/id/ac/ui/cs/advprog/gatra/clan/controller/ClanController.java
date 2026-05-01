package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.service.ClanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clans")
@RequiredArgsConstructor
public class ClanController {
    private final ClanService clanService;

    @PostMapping
    public ResponseEntity<ClanResponse> createClan(
            @Valid @RequestBody CreateClanRequest request,
            @RequestAttribute("userId") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clanService.createClan(request, userId));
    }

    @GetMapping("/{clanId}")
    public ResponseEntity<ClanResponse> getClan(@PathVariable String clanId) {
        return ResponseEntity.ok(clanService.getClan(clanId));
    }

    @DeleteMapping("/{clanId}")
    public ResponseEntity<Void> deleteClan(
            @PathVariable String clanId,
            @RequestAttribute("userId") String userId) {
        clanService.deleteClan(clanId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ClanResponse> getMyClan(
            @RequestAttribute("userId") String userId) {
        ClanResponse response = clanService.getMyClan(userId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClanResponse>> getAllClans() {
        return ResponseEntity.ok(clanService.getAllClans());
    }

    @DeleteMapping("/{clanId}/members/{targetUserId}")
    public ResponseEntity<Void> kickMember(
            @PathVariable String clanId,
            @PathVariable String targetUserId,
            @RequestAttribute("userId") String leaderId) {
        clanService.kickMember(clanId, targetUserId, leaderId);
        return ResponseEntity.noContent().build();
    }

}
