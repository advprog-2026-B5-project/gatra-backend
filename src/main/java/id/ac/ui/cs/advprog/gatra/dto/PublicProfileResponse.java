package id.ac.ui.cs.advprog.gatra.dto;

import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PublicProfileResponse {
    private UUID userId;
    private String username;
    private String displayName;
    private String photoUrl;
    private Long totalScore;
    private String currentLeagueTier;
    private List<AchievementResponse> featuredAchievements;
    private List<ClanSimpleResponse> joinedClans;
}