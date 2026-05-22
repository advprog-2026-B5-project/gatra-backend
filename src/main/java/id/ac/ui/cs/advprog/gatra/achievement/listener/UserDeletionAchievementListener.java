package id.ac.ui.cs.advprog.gatra.achievement.listener;

import id.ac.ui.cs.advprog.gatra.achievement.repository.UserAchievementRepository;
import id.ac.ui.cs.advprog.gatra.auth.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDeletionAchievementListener {

    private final UserAchievementRepository userAchievementRepository;

    @EventListener
    @Transactional
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        // When auth module announces a deletion, safely delete their achievements
        userAchievementRepository.deleteByUserId(event.userId());
    }
}