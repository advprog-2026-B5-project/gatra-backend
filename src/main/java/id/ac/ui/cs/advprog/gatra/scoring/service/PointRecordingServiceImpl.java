package id.ac.ui.cs.advprog.gatra.scoring.service;

import id.ac.ui.cs.advprog.gatra.scoring.model.PointActivityType;
import id.ac.ui.cs.advprog.gatra.scoring.model.PointHistory;
import id.ac.ui.cs.advprog.gatra.scoring.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointRecordingServiceImpl implements PointRecordingService {

    private final PointHistoryRepository pointHistoryRepository;

    @Override
    @Transactional
    public void recordPoints(String userId, String clanId, double points,
                             PointActivityType activityType, String referenceId) {

        PointHistory historyRecord = PointHistory.builder()
                .userId(userId)
                .clanId(clanId)
                .points(points)
                .activityType(activityType)
                .referenceId(referenceId)
                // Note: The 'earnedAt' field is automatically populated by
                // Hibernate's @CreationTimestamp in the PointHistory entity.
                .build();

        pointHistoryRepository.save(historyRecord);
    }
}