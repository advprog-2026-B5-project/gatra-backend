package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.helper.MissionCompletionChecker;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.clan.strategy.BuffDebuffStrategy;
import id.ac.ui.cs.advprog.gatra.scoring.model.ScoreModifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuffDebuffServiceImpl implements BuffDebuffService {

    private static final double NO_MEMBERS_COMPLETION_RATE = 0.0;

    private final ClanMembershipRepository membershipRepository;
    private final MissionCompletionChecker missionCompletionChecker;
    private final List<BuffDebuffStrategy> strategies;

    @Override
    public ScoreModifier getModifier(String clanId) {
        double completionRate = calculateMissionCompletionRate(clanId);
        BuffDebuffStrategy applicableStrategy = findApplicableStrategy(completionRate);
        return applicableStrategy.getModifier();
    }

    private double calculateMissionCompletionRate(String clanId) {
        List<ClanMembership> approvedMembers = membershipRepository
                .findByClanIdAndStatus(clanId, MembershipStatus.APPROVED);

        if (approvedMembers.isEmpty()) {
            return NO_MEMBERS_COMPLETION_RATE;
        }

        long membersCompletedMission = approvedMembers.stream()
                .filter(member -> missionCompletionChecker.hasCompletedAnyMission(member.getUserId()))
                .count();

        return (double) membersCompletedMission / approvedMembers.size();
    }

    private BuffDebuffStrategy findApplicableStrategy(double completionRate) {
        return strategies.stream()
                .filter(strategy -> strategy.isApplicable(completionRate))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No applicable buff/debuff strategy found for completion rate: " + completionRate
                ));
    }
}