package id.ac.ui.cs.advprog.gatra.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_achievements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(name = "unlocked_at", nullable = false)
    private LocalDateTime unlockedAt;

    @Column(name = "is_displayed", nullable = false)
    private boolean isDisplayed = false;

    @PrePersist
    protected void onCreate() {
        unlockedAt = LocalDateTime.now();
    }
}