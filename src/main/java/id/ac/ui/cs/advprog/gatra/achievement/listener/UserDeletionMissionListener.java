package id.ac.ui.cs.advprog.gatra.achievement.listener;

import id.ac.ui.cs.advprog.gatra.achievement.repository.UserMissionProgressRepository;
import id.ac.ui.cs.advprog.gatra.auth.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDeletionMissionListener {

    private final UserMissionProgressRepository userMissionProgressRepository;

    @EventListener
    @Transactional
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        // Clean up mission progress when a user is deleted
        userMissionProgressRepository.deleteByUserId(event.userId());
    }
}