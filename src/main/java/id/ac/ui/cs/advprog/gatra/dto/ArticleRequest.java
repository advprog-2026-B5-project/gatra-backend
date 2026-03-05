package id.ac.ui.cs.advprog.gatra.dto;

import lombok.Data;
import java.util.UUID;

public class ArticleRequest {
    private String title;
    private String content;
    private UUID categoryId;
}
