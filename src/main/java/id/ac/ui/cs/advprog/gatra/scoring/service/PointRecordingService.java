package id.ac.ui.cs.advprog.gatra.scoring.service;

import id.ac.ui.cs.advprog.gatra.scoring.model.PointActivityType;

public interface PointRecordingService {
    void recordPoints(String userId, String clanId, double points, PointActivityType activityType, String referenceId);
}