package id.ac.ui.cs.advprog.gatra.achievement.model;

import id.ac.ui.cs.advprog.gatra.model.ActionType;
import id.ac.ui.cs.advprog.gatra.model.MissionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "daily_missions")
public class DailyMission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String description;
    private Integer targetCount;
    private Integer rewardPoints;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    private MissionStatus status; // Perubahan dari boolean ke Enum
}