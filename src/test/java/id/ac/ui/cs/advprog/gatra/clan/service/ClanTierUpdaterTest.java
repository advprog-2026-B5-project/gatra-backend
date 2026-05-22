package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.event.ClanReachedHighestTierEvent;
import id.ac.ui.cs.advprog.gatra.clan.exception.ClanNotFoundException;
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
class ClanTierUpdaterTest {

    @Mock private ClanRepository clanRepository;
    @Mock private ClanMembershipRepository membershipRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private ClanTierUpdater clanTierUpdater;

    @Test
    void updateClanTier_success_tierUpdatedAndSaved() {
        Clan clan = Clan.builder().id("c1").tier("SILVER").build();
        when(clanRepository.findById("c1")).thenReturn(Optional.of(clan));

        clanTierUpdater.updateClanTier("c1", ClanTier.GOLD);

        assertEquals(ClanTier.GOLD.name(), clan.getTier());
        verify(clanRepository).save(clan);
    }

    @Test
    void updateClanTier_clanNotFound_throwsClanNotFoundException() {
        when(clanRepository.findById("c1")).thenReturn(Optional.empty());

        assertThrows(ClanNotFoundException.class,
                () -> clanTierUpdater.updateClanTier("c1", ClanTier.GOLD));
        verify(clanRepository, never()).save(any());
    }

    @Test
    void updateClanTier_promotedToHighestTier_publishesEvent() {
        Clan clan = Clan.builder().id("c1").tier("GOLD").build();
        ClanMembership mem1 = ClanMembership.builder().userId("user1").build();
        ClanMembership mem2 = ClanMembership.builder().userId("user2").build();

        when(clanRepository.findById("c1")).thenReturn(Optional.of(clan));
        when(membershipRepository.findByClanIdAndStatus("c1", MembershipStatus.APPROVED))
                .thenReturn(List.of(mem1, mem2));

        clanTierUpdater.updateClanTier("c1", ClanTier.DIAMOND);

        verify(eventPublisher).publishEvent(any(ClanReachedHighestTierEvent.class));
    }

    @Test
    void updateClanTier_promotedToHighestTier_eventContainsCorrectData() {
        Clan clan = Clan.builder().id("c1").tier("GOLD").build();
        ClanMembership mem = ClanMembership.builder().userId("user1").build();

        when(clanRepository.findById("c1")).thenReturn(Optional.of(clan));
        when(membershipRepository.findByClanIdAndStatus("c1", MembershipStatus.APPROVED))
                .thenReturn(List.of(mem));

        clanTierUpdater.updateClanTier("c1", ClanTier.DIAMOND);

        verify(eventPublisher).publishEvent(argThat(event -> {
            ClanReachedHighestTierEvent e = (ClanReachedHighestTierEvent) event;
            return e.getClanId().equals("c1") && e.getMemberIds().contains("user1");
        }));
    }

    @Test
    void updateClanTier_alreadyHighestTier_doesNotPublishEvent() {
        Clan clan = Clan.builder().id("c1").tier("DIAMOND").build();
        when(clanRepository.findById("c1")).thenReturn(Optional.of(clan));

        clanTierUpdater.updateClanTier("c1", ClanTier.DIAMOND);

        verify(eventPublisher, never()).publishEvent(any(ClanReachedHighestTierEvent.class));
        verify(membershipRepository, never()).findByClanIdAndStatus(any(), any());
    }

    @Test
    void updateClanTier_notHighestTier_doesNotPublishEvent() {
        Clan clan = Clan.builder().id("c1").tier("SILVER").build();
        when(clanRepository.findById("c1")).thenReturn(Optional.of(clan));

        clanTierUpdater.updateClanTier("c1", ClanTier.GOLD);

        verify(eventPublisher, never()).publishEvent(any());
        verify(membershipRepository, never()).findByClanIdAndStatus(any(), any());
    }
}