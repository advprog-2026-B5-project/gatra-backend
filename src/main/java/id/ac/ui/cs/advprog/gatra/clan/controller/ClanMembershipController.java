package id.ac.ui.cs.advprog.gatra.clan.controller;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;
import id.ac.ui.cs.advprog.gatra.clan.service.ClanMembershipService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/clans/{clanId}/applications")
@RequiredArgsConstructor
public class ClanMembershipController {
    private final ClanMembershipService membershipService;

    @PostMapping
    public ResponseEntity<MembershipResponse> apply(
            @PathVariable String clanId,
            @RequestAttribute("userId") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipService.applyToClan(clanId, userId));
    }

    @GetMapping
    public ResponseEntity<List<MembershipResponse>> getPending(
            @PathVariable String clanId,
            @RequestAttribute("userId") String leaderId) {
        return ResponseEntity.ok(membershipService.getPendingApplications(clanId, leaderId));
    }

    @PatchMapping("/{applicantId}")
    public ResponseEntity<MembershipResponse> decide(
            @PathVariable String clanId,
            @PathVariable String applicantId,
            @Valid @RequestBody MembershipDecisionRequest request,
            @RequestAttribute("userId") String leaderId) {
        return ResponseEntity.ok(
                membershipService.decideMembership(clanId, applicantId, request, leaderId));
    }

    @DeleteMapping
    public ResponseEntity<Void> leaveClan(
            @PathVariable String clanId,
            @RequestAttribute("userId") String userId) {
        membershipService.leaveClan(clanId, userId);
        return ResponseEntity.noContent().build();
    }
}
