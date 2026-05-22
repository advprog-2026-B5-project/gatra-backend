package id.ac.ui.cs.advprog.gatra.article.service;

import id.ac.ui.cs.advprog.gatra.article.dto.ArticleRequest;
import id.ac.ui.cs.advprog.gatra.article.dto.ArticleResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.article.mapper.ArticleMapper;
import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.article.model.Category;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.achievement.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.achievement.service.MilestoneService;
import id.ac.ui.cs.advprog.gatra.achievement.service.MissionProgressService;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.article.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.article.repository.CategoryRepository;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;
    private final MilestoneService milestoneService;
    private final MissionProgressService missionProgressService;

    private static final String ARTICLE_ENTITY = "Article";

    @Override
    public List<ArticleResponse> getAllArticles() {
        return articleRepository.findAll().stream()
                .filter(article -> !article.isDeleted())
                .map(articleMapper::toResponse)
                .toList();
    }

    @Override
    public ArticleResponse getArticleById(UUID id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ARTICLE_ENTITY, id));
        if (article.isDeleted()) {
            throw new ResourceNotFoundException(ARTICLE_ENTITY, id);
        }
        return articleMapper.toResponse(article);

    }

    @Override
    @Transactional
    public ArticleResponse createArticle(ArticleRequest request, String username) {
        User user = findUserOrThrow(username);
        Category category = findCategoryOrThrow(request.getCategoryId());

        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .createdBy(user)
                .build();

        return articleMapper.toResponse(articleRepository.save(article));
    }

    @Override
    @Transactional
    public ArticleResponse updateArticle(UUID id, ArticleRequest request) {
        Article article = findArticleOrThrow(id);
        Category category = findCategoryOrThrow(request.getCategoryId());

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setCategory(category);

        return articleMapper.toResponse(articleRepository.save(article));
    }

    @Override
    @Transactional
    public void deleteArticle(UUID id, String adminUsername) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ARTICLE_ENTITY, id));
        if (article.isDeleted()) {
            throw new IllegalStateException("Artikel sudah dihapus pada " + article.getDeletedAt());
        }
        article.softDelete(adminUsername);
        articleRepository.save(article);
    }

    @Override
    public List<ArticleResponse> getDeletedArticles() {
        return articleRepository.findAll().stream()
                .filter(Article::isDeleted)
                .map(articleMapper::toResponse)
                .toList();
    }

    private Article findArticleOrThrow(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ARTICLE_ENTITY, id));
    }

    private Category findCategoryOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
    }

    @Override
    public MilestoneResponse markAsRead(UUID articleId, String username) {
        User user = findUserOrThrow(username);
        var completedMissions = missionProgressService.incrementProgress(user.getId(), "READ_ARTICLE");
        MilestoneResponse response = milestoneService.recordAction(user.getId(), ActionType.READ_ARTICLE);
        response.setCompletedMissions(completedMissions);
        return response;
    }
}
