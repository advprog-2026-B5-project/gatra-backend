package id.ac.ui.cs.advprog.gatra.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
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

    @Enumerated(EnumType.STRING)
    private ActionType category;

    @Column(name = "milestone_threshold", nullable = false)
    private Integer milestoneThreshold;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "badge_url")
    private String badgeUrl;

    @OneToMany(mappedBy = "achievement", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<UserAchievement> userAchievements = new HashSet<>();
}