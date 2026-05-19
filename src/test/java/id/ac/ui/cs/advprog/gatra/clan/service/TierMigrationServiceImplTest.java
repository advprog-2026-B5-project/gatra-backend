package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.gatra.clan.dto.TierLeaderboardResponse;
import id.ac.ui.cs.advprog.gatra.clan.event.ClanReachedDiamondEvent;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanTier;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TierMigrationServiceImplTest {

    @Mock private ClanRepository clanRepository;
    @Mock private ClanMembershipRepository membershipRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private TierMigrationServiceImpl tierMigrationService;

    @Test
    void migrate_promotesAndRelegatesProperly() {
        LeaderboardEntryResponse rank1 = LeaderboardEntryResponse.builder().clanId("c1").rank(1).build(); // Promoted
        LeaderboardEntryResponse rank2 = LeaderboardEntryResponse.builder().clanId("c2").rank(2).build(); // Promoted
        LeaderboardEntryResponse rank3 = LeaderboardEntryResponse.builder().clanId("c3").rank(3).build(); // Promoted
        LeaderboardEntryResponse rank4 = LeaderboardEntryResponse.builder().clanId("c4").rank(4).build(); // Stays
        LeaderboardEntryResponse rank5 = LeaderboardEntryResponse.builder().clanId("c5").rank(5).build(); // Stays
        LeaderboardEntryResponse rank6 = LeaderboardEntryResponse.builder().clanId("c6").rank(6).build(); // Relegated
        LeaderboardEntryResponse rank7 = LeaderboardEntryResponse.builder().clanId("c7").rank(7).build(); // Relegated
        LeaderboardEntryResponse rank8 = LeaderboardEntryResponse.builder().clanId("c8").rank(8).build(); // Relegated

        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier(ClanTier.SILVER.name())
                .rankings(List.of(rank1, rank2, rank3, rank4, rank5, rank6, rank7, rank8))
                .build();

        Clan clan1 = Clan.builder().id("c1").tier("SILVER").build();
        Clan clan4 = Clan.builder().id("c4").tier("SILVER").build();
        Clan clan8 = Clan.builder().id("c8").tier("SILVER").build();

        when(clanRepository.findById("c1")).thenReturn(Optional.of(clan1));
        when(clanRepository.findById("c2")).thenReturn(Optional.of(Clan.builder().tier("SILVER").build()));
        when(clanRepository.findById("c3")).thenReturn(Optional.of(Clan.builder().tier("SILVER").build()));
        when(clanRepository.findById("c4")).thenReturn(Optional.of(clan4));
        when(clanRepository.findById("c5")).thenReturn(Optional.of(Clan.builder().tier("SILVER").build()));
        when(clanRepository.findById("c6")).thenReturn(Optional.of(Clan.builder().tier("SILVER").build()));
        when(clanRepository.findById("c7")).thenReturn(Optional.of(Clan.builder().tier("SILVER").build()));
        when(clanRepository.findById("c8")).thenReturn(Optional.of(clan8));

        tierMigrationService.migrate(List.of(tierBoard));

        assertEquals(ClanTier.GOLD.name(), clan1.getTier());
        assertEquals(ClanTier.SILVER.name(), clan4.getTier());
        assertEquals(ClanTier.BRONZE.name(), clan8.getTier());

        verify(clanRepository, times(8)).save(any(Clan.class));
    }

    @Test
    void migrate_publishDiamondEventWhenPromotedToDiamond() {
        LeaderboardEntryResponse rank1 = LeaderboardEntryResponse.builder().clanId("c1").rank(1).build();
        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier(ClanTier.GOLD.name())
                .rankings(List.of(rank1))
                .build();

        Clan clan1 = Clan.builder().id("c1").tier("GOLD").build();
        when(clanRepository.findById("c1")).thenReturn(Optional.of(clan1));

        ClanMembership mem = ClanMembership.builder().userId("user1").build();
        when(membershipRepository.findByClanIdAndStatus("c1", MembershipStatus.APPROVED)).thenReturn(List.of(mem));

        tierMigrationService.migrate(List.of(tierBoard));

        assertEquals(ClanTier.DIAMOND.name(), clan1.getTier());
        verify(eventPublisher, times(1)).publishEvent(any(ClanReachedDiamondEvent.class));
    }
    @Test
    void updateClanTier_clanNotFound() {
        LeaderboardEntryResponse rank1 = LeaderboardEntryResponse.builder().clanId("c1").rank(1).build();
        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier(ClanTier.GOLD.name())
                .rankings(List.of(rank1))
                .build();

        when(clanRepository.findById("c1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tierMigrationService.migrate(List.of(tierBoard)));
    }

    @Test
    void migrate_staleDiamond_doesNotPublishEvent() {
        LeaderboardEntryResponse rank1 = LeaderboardEntryResponse.builder().clanId("c1").rank(1).build();
        TierLeaderboardResponse tierBoard = TierLeaderboardResponse.builder()
                .tier(ClanTier.DIAMOND.name())
                .rankings(List.of(rank1))
                .build();

        Clan clan1 = Clan.builder().id("c1").tier("DIAMOND").build();
        when(clanRepository.findById("c1")).thenReturn(Optional.of(clan1));

        tierMigrationService.migrate(List.of(tierBoard));

        assertEquals(ClanTier.DIAMOND.name(), clan1.getTier());
        verify(eventPublisher, never()).publishEvent(any(ClanReachedDiamondEvent.class));
    }
}
