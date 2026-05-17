package id.ac.ui.cs.advprog.gatra.social.service;

import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.achievement.dto.AchievementResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.ClanSimpleResponse;
import id.ac.ui.cs.advprog.gatra.auth.dto.PublicProfileResponse;
import id.ac.ui.cs.advprog.gatra.auth.dto.UserSearchResponse;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.achievement.mapper.AchievementMapper;
import id.ac.ui.cs.advprog.gatra.auth.model.StudentProfile;
import id.ac.ui.cs.advprog.gatra.auth.model.User;
import id.ac.ui.cs.advprog.gatra.auth.repository.StudentProfileRepository;
import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialServiceImpl implements SocialService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final ClanMembershipRepository clanMembershipRepository;
    private final AchievementMapper achievementMapper;

    @Override
    public List<UserSearchResponse> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return userRepository.findByUsernameContainingIgnoreCase(query.trim())
                .stream()
                .map(user -> UserSearchResponse.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .displayName(user.getDisplayName())
                        .photoUrl(null) // Can map to an avatar generation logic if needed
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public PublicProfileResponse getPublicProfile(String username) {
        // 1. Fetch Core User
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));

        // 2. Fetch Profile & Score
        StudentProfile profile = studentProfileRepository.findById(user.getId()).orElse(null);
        double totalScore = pointHistoryRepository.sumPointsByUserId(user.getId().toString());

        // 3. Fetch Featured Achievements (Max 3, explicitly mapped)
        List<AchievementResponse> featuredAchievements = userAchievementRepository
                .findByUserUsernameAndIsDisplayedTrue(username)
                .stream()
                .limit(3)
                .map(achievementMapper::toResponseFromUserAchievement)
                .collect(Collectors.toList());

        // 4. Fetch Clan Memberships (Approved Only)
        Optional<ClanMembership> membershipOpt = clanMembershipRepository
                .findFirstByUserIdAndStatus(user.getId().toString(), MembershipStatus.APPROVED);

        List<ClanSimpleResponse> joinedClans = membershipOpt.map(membership ->
                Collections.singletonList(ClanSimpleResponse.builder()
                        .id(membership.getClan().getId())
                        .name(membership.getClan().getName())
                        .role(membership.getRole().name())
                        .build())
        ).orElse(Collections.emptyList());

        // 5. Aggregate to Response
        return PublicProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .photoUrl(null)
                .totalScore(Math.round(totalScore))
                .currentLeagueTier(profile != null && profile.getCurrentLeagueTier() != null ? profile.getCurrentLeagueTier() : "Bronze")
                .featuredAchievements(featuredAchievements)
                .joinedClans(joinedClans)
                .build();
    }
}