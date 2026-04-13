package id.ac.ui.cs.advprog.gatra.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "daily_missions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyMission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer targetCount;

    @Column(nullable = false)
    private String actionType; // Contoh: "READ_ARTICLE", "FINISH_QUIZ"

    @Column(nullable = false)
    private boolean isActive; // Menandakan apakah misi masuk di rotasi hari ini
}