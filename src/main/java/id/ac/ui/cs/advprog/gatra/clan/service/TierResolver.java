package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.model.ClanTier;
import org.springframework.stereotype.Component;

@Component
public class TierResolver {

    private static final int PROMOTION_COUNT = 3;
    private static final int RELEGATION_COUNT = 3;
    private static final ClanTier[] TIERS = ClanTier.values();

    public ClanTier resolveNewTier(ClanTier currentTier, int rank, int totalClans) {
        if (isEligibleForPromotion(rank)) {
            return promoteTier(currentTier);
        }
        if (isEligibleForRelegation(rank, totalClans)) {
            return relegateTier(currentTier);
        }
        return currentTier;
    }

    private boolean isEligibleForPromotion(int rank) {
        return rank <= PROMOTION_COUNT;
    }

    private boolean isEligibleForRelegation(int rank, int totalClans) {
        return !isEligibleForPromotion(rank) && rank > totalClans - RELEGATION_COUNT;
    }

    private ClanTier promoteTier(ClanTier currentTier) {
        int nextIndex = currentTier.ordinal() + 1;
        return nextIndex < TIERS.length ? TIERS[nextIndex] : currentTier;
    }

    private ClanTier relegateTier(ClanTier currentTier) {
        int prevIndex = currentTier.ordinal() - 1;
        return prevIndex >= 0 ? TIERS[prevIndex] : currentTier;
    }
}