package id.ac.ui.cs.advprog.gatra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admin_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminProfile {

    @Id
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // TODO: hal-hal yang diperlukan admin
}
