package id.ac.ui.cs.advprog.gatra.clan.model;

public enum ClanTier {
    BRONZE,
    SILVER,
    GOLD,
    DIAMOND;

    public boolean isHighest() {
        return this == values()[values().length - 1];
    }
}