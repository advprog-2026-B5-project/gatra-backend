package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.gatra.achievement.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.gatra.achievement.service.DailyMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/daily-missions")
@RequiredArgsConstructor
public class AdminMissionController {

    private final DailyMissionService dailyMissionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DailyMissionResponse> createMission(@RequestBody DailyMissionRequest request) {
        return new ResponseEntity<>(dailyMissionService.createMission(request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DailyMissionResponse>> getAllMissions() {
        return ResponseEntity.ok(dailyMissionService.getAllMissions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DailyMissionResponse> getMissionById(@PathVariable UUID id) {
        return ResponseEntity.ok(dailyMissionService.getMissionById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DailyMissionResponse> updateMission(@PathVariable UUID id, @RequestBody DailyMissionRequest request) {
        return ResponseEntity.ok(dailyMissionService.updateMission(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMission(@PathVariable UUID id) {
        dailyMissionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }
}