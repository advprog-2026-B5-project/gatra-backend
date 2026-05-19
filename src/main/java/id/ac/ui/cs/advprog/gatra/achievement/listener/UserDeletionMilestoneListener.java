package id.ac.ui.cs.advprog.gatra.achievement.listener;

import id.ac.ui.cs.advprog.gatra.achievement.repository.StudentMilestoneProgressRepository;
import id.ac.ui.cs.advprog.gatra.auth.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDeletionMilestoneListener {

    private final StudentMilestoneProgressRepository studentMilestoneProgressRepository;

    @EventListener
    @Transactional
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        // Clean up milestone progress when a user is deleted
        studentMilestoneProgressRepository.deleteByUserId(event.userId());
    }
}