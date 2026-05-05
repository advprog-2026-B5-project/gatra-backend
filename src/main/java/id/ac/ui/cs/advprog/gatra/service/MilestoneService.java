package id.ac.ui.cs.advprog.gatra.service;

import id.ac.ui.cs.advprog.gatra.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.model.ActionType;

import java.util.UUID;

public interface MilestoneService {
    MilestoneResponse recordAction(UUID userId, ActionType actionType);
}
