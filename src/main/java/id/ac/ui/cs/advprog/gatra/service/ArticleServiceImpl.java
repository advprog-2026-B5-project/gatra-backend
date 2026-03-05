package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.ArticleRequest;
import id.ac.ui.cs.advprog.gatra.dto.ArticleResponse;
import id.ac.ui.cs.advprog.gatra.mapper.ArticleMapper;
import id.ac.ui.cs.advprog.gatra.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.repository.CategoryRepository;
import id.ac.ui.cs.advprog.gatra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;

    @Override
    public List<ArticleResponse> getAllArticles() {
        return null; // TODO
    }

    @Override
    public ArticleResponse getArticleById(UUID id) {
        return null; // TODO
    }

    @Override
    public ArticleResponse createArticle(ArticleRequest request, String username) {
        return null; // TODO
    }

    @Override
    public ArticleResponse updateArticle(UUID id, ArticleRequest request) {
        return null; // TODO
    }

    @Override
    public void deleteArticle(UUID id) {
        // TODO
    }
}