package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.event.ClanReachedHighestTierEvent;
import id.ac.ui.cs.advprog.gatra.clan.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanTier;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClanTierUpdater {

    private final ClanRepository clanRepository;
    private final ClanMembershipRepository membershipRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void updateClanTier(String clanId, ClanTier newTier) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new ClanNotFoundException(clanId));

        ClanTier oldTier = ClanTier.valueOf(clan.getTier());
        clan.setTier(newTier.name());
        clanRepository.save(clan);

        publishHighestTierEventIfNeeded(clanId, oldTier, newTier);
    }

    private void publishHighestTierEventIfNeeded(String clanId, ClanTier oldTier, ClanTier newTier) {
        if (newTier.isHighest() && !oldTier.isHighest()) {
            List<String> memberIds = membershipRepository
                    .findByClanIdAndStatus(clanId, MembershipStatus.APPROVED)
                    .stream()
                    .map(ClanMembership::getUserId)
                    .toList();
            eventPublisher.publishEvent(
                    new ClanReachedHighestTierEvent(this, clanId, memberIds));
        }
    }
}
