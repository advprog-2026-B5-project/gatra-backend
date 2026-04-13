package id.ac.ui.cs.advprog.gatra.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "achievements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String category;

    @Column(name = "milestone_threshold", nullable = false)
    private Integer milestoneThreshold;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "badge_url")
    private String badgeUrl;
}