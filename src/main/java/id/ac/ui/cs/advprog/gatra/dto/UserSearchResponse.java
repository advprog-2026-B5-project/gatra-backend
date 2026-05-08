package id.ac.ui.cs.advprog.gatra.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class UserSearchResponse {
    private UUID userId;
    private String username;
    private String displayName;
    private String photoUrl;
}