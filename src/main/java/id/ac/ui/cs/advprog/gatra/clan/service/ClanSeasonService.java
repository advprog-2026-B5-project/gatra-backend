package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.dto.SeasonResultResponse;

public interface ClanSeasonService {
    SeasonResultResponse endSeason();
    SeasonResultResponse getLastSeasonResult();
}