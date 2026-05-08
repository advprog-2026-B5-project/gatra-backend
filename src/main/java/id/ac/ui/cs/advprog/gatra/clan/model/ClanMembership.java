package id.ac.ui.cs.advprog.gatra.clan.model;

import id.ac.ui.cs.advprog.gatra.clan.state.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clan_memberships")
@Getter @Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ClanMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    private Clan clan;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ClanRole role = ClanRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MembershipStatus status = MembershipStatus.PENDING;

    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Transient
    @Builder.Default
    private MembershipState state = new PendingState();

    @PostLoad
    private void initState() {
        this.state = switch (this.status) {
            case PENDING   -> new PendingState();
            case APPROVED  -> new ApprovedState();
            case REJECTED  -> new RejectedState();
        };
    }

    public void approve() { state.approve(this); }
    public void reject()  { state.reject(this); }
}