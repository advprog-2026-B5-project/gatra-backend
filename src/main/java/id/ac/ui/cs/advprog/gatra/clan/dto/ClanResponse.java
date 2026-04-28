package id.ac.ui.cs.advprog.gatra.clan.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class ClanResponse {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
