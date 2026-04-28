package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.*;

public interface ClanService {
    ClanResponse createClan(CreateClanRequest request, String userId);
    ClanResponse getClan(String clanId);
    void deleteClan(String clanId, String userId);
}
