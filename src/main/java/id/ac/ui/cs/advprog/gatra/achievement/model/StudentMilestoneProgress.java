package id.ac.ui.cs.advprog.gatra.achievement.model;

import id.ac.ui.cs.advprog.gatra.model.ActionType;
import id.ac.ui.cs.advprog.gatra.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "student_milestone_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "action_type"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentMilestoneProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Builder.Default
    @Column(name = "total_count", nullable = false)
    private Integer totalCount = 0;
}
