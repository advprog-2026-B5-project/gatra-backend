package id.ac.ui.cs.advprog.gatra.clan.listener;

import id.ac.ui.cs.advprog.gatra.clan.event.SeasonEndedEvent;
import id.ac.ui.cs.advprog.gatra.clan.service.TierMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeasonEndedListener {
    private final TierMigrationService tierMigrationService;

    @EventListener
    public void onSeasonEnded(SeasonEndedEvent event) {
        tierMigrationService.migrate(event.getLeaderboards());
    }
}