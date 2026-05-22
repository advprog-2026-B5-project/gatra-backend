package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.model.Clan;

public interface ClanValidator {
    Clan findClanOrThrow(String clanId);
    void validateLeader(String clanId, String userId);
    void validateUserNotInAnyClan(String userId);
    void validateNotSelfKick(String leaderId, String targetUserId);
}