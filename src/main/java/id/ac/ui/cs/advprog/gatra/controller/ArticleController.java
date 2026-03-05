package id.ac.ui.cs.advprog.gatra.controller;

import id.ac.ui.cs.advprog.gatra.dto.ArticleRequest;
import id.ac.ui.cs.advprog.gatra.dto.ArticleResponse;
import id.ac.ui.cs.advprog.gatra.service.ArticleService;
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

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticles() {
        return null; // TODO
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable UUID id) {
        return null; // TODO
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
            @RequestBody ArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return null; // TODO
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable UUID id,
            @RequestBody ArticleRequest request) {
        return null; // TODO
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID id) {
        return null; // TODO
    }
}