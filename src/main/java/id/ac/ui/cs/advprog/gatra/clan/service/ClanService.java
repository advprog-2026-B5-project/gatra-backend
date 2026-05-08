package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;

import java.util.List;

public interface ClanService {
    ClanResponse createClan(CreateClanRequest request, String userId);
    ClanResponse getClan(String clanId);
    void deleteClan(String clanId, String userId);
    ClanResponse getMyClan(String userId);
    List<ClanResponse> getAllClans();
    void kickMember(String clanId, String targetUserId, String leaderId);
}
