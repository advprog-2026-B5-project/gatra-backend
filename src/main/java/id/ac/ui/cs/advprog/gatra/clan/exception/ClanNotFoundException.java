package id.ac.ui.cs.advprog.gatra.clan.exception;

public class ClanNotFoundException extends RuntimeException {
    public ClanNotFoundException(String clanId) {
        super("Clan tidak ditemukan: " + clanId);
    }
}