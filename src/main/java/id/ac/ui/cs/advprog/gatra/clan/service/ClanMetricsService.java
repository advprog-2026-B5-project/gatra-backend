package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.model.ClanTier;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClanMetricsService {

    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;
    private final MeterRegistry registry;

    @Getter private Counter clanCreatedCounter;
    @Getter private Counter clanDeletedCounter;
    @Getter private Counter membershipAppliedCounter;
    @Getter private Counter membershipApprovedCounter;
    @Getter private Counter membershipRejectedCounter;
    @Getter private Counter membershipLeftCounter;
    @Getter private Counter membershipKickedCounter;
    @Getter private Counter leaderboardViewedCounter;
    @Getter private Counter leaderboardByTierViewedCounter;
    @Getter private Counter seasonResetCounter;

    @jakarta.annotation.PostConstruct
    public void registerMetrics() {

        Gauge.builder("gatra.clan.total", clanRepository, repo -> repo.count())
                .description("Total jumlah clan yang ada")
                .register(registry);

        Gauge.builder("gatra.clan.members.active.total", membershipRepository,
                        repo -> repo.countByClanIdAndStatus(null, MembershipStatus.APPROVED))
                .description("Total member aktif di semua clan")
                .register(registry);

        for (ClanTier tier : ClanTier.values()) {
            String tierName = tier.name();
            Gauge.builder("gatra.clan.tier.count", clanRepository,
                            repo -> repo.findByTier(tierName).size())
                    .description("Jumlah clan di tier " + tierName)
                    .tag("tier", tierName)
                    .register(registry);
        }

        Gauge.builder("gatra.clan.membership.pending.total", membershipRepository,
                        repo -> repo.findAll().stream()
                                .filter(m -> m.getStatus() == MembershipStatus.PENDING)
                                .count())
                .description("Total request bergabung yang sedang pending")
                .register(registry);

        clanCreatedCounter = Counter.builder("gatra.clan.created.total")
                .description("Total clan yang berhasil dibuat").register(registry);
        clanDeletedCounter = Counter.builder("gatra.clan.deleted.total")
                .description("Total clan yang dihapus").register(registry);
        membershipAppliedCounter = Counter.builder("gatra.clan.membership.applied.total")
                .description("Total aplikasi bergabung ke clan").register(registry);
        membershipApprovedCounter = Counter.builder("gatra.clan.membership.approved.total")
                .description("Total aplikasi yang disetujui").register(registry);
        membershipRejectedCounter = Counter.builder("gatra.clan.membership.rejected.total")
                .description("Total aplikasi yang ditolak").register(registry);
        membershipLeftCounter = Counter.builder("gatra.clan.membership.left.total")
                .description("Total member yang keluar dari clan").register(registry);
        membershipKickedCounter = Counter.builder("gatra.clan.membership.kicked.total")
                .description("Total member yang di-kick dari clan").register(registry);
        leaderboardViewedCounter = Counter.builder("gatra.clan.leaderboard.viewed.total")
                .description("Total request lihat semua leaderboard").register(registry);
        leaderboardByTierViewedCounter = Counter.builder("gatra.clan.leaderboard.tier.viewed.total")
                .description("Total request lihat leaderboard per tier").register(registry);
        seasonResetCounter = Counter.builder("gatra.clan.season.reset.total")
                .description("Total pergantian musim yang dilakukan admin").register(registry);
    }
}