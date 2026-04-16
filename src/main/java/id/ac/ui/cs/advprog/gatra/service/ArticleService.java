package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.ArticleRequest;
import id.ac.ui.cs.advprog.gatra.dto.ArticleResponse;
import java.util.List;
import java.util.UUID;

public interface ArticleService {
    List<ArticleResponse> getAllArticles();
    ArticleResponse getArticleById(UUID id);
    ArticleResponse createArticle(ArticleRequest request, String username);
    ArticleResponse updateArticle(UUID id, ArticleRequest request);
    void deleteArticle(UUID id);
}