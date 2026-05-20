package id.ac.ui.cs.advprog.gatra.clan.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "season_snapshots")
@Getter @Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SeasonSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String clanId;

    @Column(nullable = false)
    private String clanName;

    @Column(nullable = false)
    private String tier;

    private double finalScore;

    private int finalRank;

    @Builder.Default
    private LocalDateTime snapshotAt = LocalDateTime.now();

    @Column(nullable = false)
    private int seasonNumber;
}