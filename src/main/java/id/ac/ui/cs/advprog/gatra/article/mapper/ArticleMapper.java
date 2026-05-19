package id.ac.ui.cs.advprog.gatra.article.mapper;

import id.ac.ui.cs.advprog.gatra.article.dto.ArticleResponse;
import id.ac.ui.cs.advprog.gatra.article.model.Article;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {
    public ArticleResponse toResponse(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .categoryId(article.getCategory().getId())
                .categoryName(article.getCategory().getName())
                .createdBy(article.getCreatedBy().getUsername())
                .build();
    }
}