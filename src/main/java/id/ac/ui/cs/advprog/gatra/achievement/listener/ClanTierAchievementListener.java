package id.ac.ui.cs.advprog.gatra.achievement.listener;

import id.ac.ui.cs.advprog.gatra.achievement.model.Achievement;
import id.ac.ui.cs.advprog.gatra.achievement.model.AchievementConstants;
import id.ac.ui.cs.advprog.gatra.achievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.gatra.achievement.service.UserAchievementService;
import id.ac.ui.cs.advprog.gatra.clan.event.ClanReachedDiamondEvent;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClanTierAchievementListener {

    private final AchievementRepository achievementRepository;
    private final UserAchievementService userAchievementService;

    @EventListener
    @Transactional
    public void onClanReachedDiamond(ClanReachedDiamondEvent event) {
        Achievement diamondAchievement = achievementRepository
                .findByName(AchievementConstants.DIAMOND_CLAN_ACHIEVEMENT_NAME)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement", AchievementConstants.DIAMOND_CLAN_ACHIEVEMENT_NAME));

        for (String memberId : event.getMemberIds()) {
            UUID userId = UUID.fromString(memberId);
            userAchievementService.unlockIfNotYet(userId, diamondAchievement);
        }
    }
}