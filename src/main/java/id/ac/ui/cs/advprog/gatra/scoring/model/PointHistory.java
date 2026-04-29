package id.ac.ui.cs.advprog.gatra.scoring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "point_histories", indexes = {
        @Index(name = "idx_point_history_clan_date", columnList = "clan_id, earned_at")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "clan_id", nullable = false)
    private String clanId;

    @Column(nullable = false)
    private double points;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private PointActivityType activityType;

    /**
     * The ID of the specific entity that triggered this point gain.
     * e.g., the Quiz ID or Achievement ID.
     */
    @Column(name = "reference_id")
    private String referenceId;

    @CreationTimestamp
    @Column(name = "earned_at", nullable = false, updatable = false)
    private LocalDateTime earnedAt;
}