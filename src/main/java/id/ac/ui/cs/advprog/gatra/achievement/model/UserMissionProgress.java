package id.ac.ui.cs.advprog.gatra.achievement.model;

import id.ac.ui.cs.advprog.gatra.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_mission_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "mission_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMissionProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private DailyMission mission;

    @Builder.Default
    @Column(name = "current_count")
    private Integer currentCount = 0;

    @Builder.Default
    @Column(name = "is_claimed")
    private Boolean isClaimed = false;
}