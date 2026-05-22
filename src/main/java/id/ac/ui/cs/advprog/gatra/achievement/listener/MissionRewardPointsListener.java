package id.ac.ui.cs.advprog.gatra.achievement.listener;

import id.ac.ui.cs.advprog.gatra.achievement.event.MissionRewardClaimedEvent;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.scoring.model.PointActivityType;
import id.ac.ui.cs.advprog.gatra.scoring.service.PointRecordingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MissionRewardPointsListener {

    private final ClanMembershipRepository clanMembershipRepository;
    private final PointRecordingService pointRecordingService;

    @EventListener
    @Transactional
    public void onMissionRewardClaimed(MissionRewardClaimedEvent event) {
        if (event.rewardPoints() <= 0) {
            return;
        }

        String userId = event.userId().toString();
        String missionId = event.missionId().toString();

        clanMembershipRepository.findFirstByUserIdAndStatus(userId, MembershipStatus.APPROVED)
                .ifPresent(membership -> pointRecordingService.recordPoints(
                        userId,
                        membership.getClan().getId(),
                        event.rewardPoints(),
                        PointActivityType.DAILY_MISSION_COMPLETED,
                        missionId
                ));
    }
}
