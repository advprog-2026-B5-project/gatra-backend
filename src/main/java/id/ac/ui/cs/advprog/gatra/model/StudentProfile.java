package id.ac.ui.cs.advprog.gatra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private Long totalScore = 0L;

    private Integer currentLevel = 1;

    private String currentLeagueTier = "Bronze";

    // TODO: Implement integrasi dengan Clan dan lainnya

    private boolean isClanLeader = false;
}
