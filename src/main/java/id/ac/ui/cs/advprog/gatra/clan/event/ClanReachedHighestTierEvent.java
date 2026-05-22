package id.ac.ui.cs.advprog.gatra.clan.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class ClanReachedHighestTierEvent extends ApplicationEvent {
    private final String clanId;
    private final List<String> memberIds;

    public ClanReachedHighestTierEvent(Object source, String clanId, List<String> memberIds) {
        super(source);
        this.clanId = clanId;
        this.memberIds = memberIds;
    }
}
