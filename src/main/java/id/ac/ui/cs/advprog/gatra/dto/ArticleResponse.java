package id.ac.ui.cs.advprog.gatra.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ArticleResponse {
    private UUID id;
    private String title;
    private String content;
    private UUID categoryId;
    private String categoryName;
    private String createdBy;
}