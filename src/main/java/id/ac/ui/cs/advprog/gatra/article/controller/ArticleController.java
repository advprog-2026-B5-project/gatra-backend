package id.ac.ui.cs.advprog.gatra.article.controller;

import id.ac.ui.cs.advprog.gatra.article.dto.ArticleRequest;
import id.ac.ui.cs.advprog.gatra.article.dto.ArticleResponse;
import id.ac.ui.cs.advprog.gatra.achievement.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.article.service.ArticleService;
import id.ac.ui.cs.advprog.gatra.achievement.service.MilestoneService;
import id.ac.ui.cs.advprog.gatra.achievement.service.MissionProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final MilestoneService milestoneService;
    private final UserRepository userRepository;
    private final MissionProgressService missionProgressService;

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

   @GetMapping("/{id}")
   public ResponseEntity<ArticleResponse> getArticleById(@PathVariable UUID id) {
       return ResponseEntity.ok(articleService.getArticleById(id));
   }

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
            @RequestBody ArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(articleService.createArticle(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable UUID id,
            @RequestBody ArticleRequest request) {
        return ResponseEntity.ok(articleService.updateArticle(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        articleService.deleteArticle(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
  
    @PostMapping("/{id}/read")
    public ResponseEntity<MilestoneResponse> markArticleAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        articleService.getArticleById(id);

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", userDetails.getUsername()));

        var completedMissions = missionProgressService.incrementProgress(user.getId(), "READ_ARTICLE");
        MilestoneResponse response = milestoneService.recordAction(user.getId(), ActionType.READ_ARTICLE);
        response.setCompletedMissions(completedMissions);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<ArticleResponse>> getDeletedArticles() {
        return ResponseEntity.ok(articleService.getDeletedArticles());
    }

}

