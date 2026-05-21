package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.decorator.BaseScoreCalculator;
import id.ac.ui.cs.advprog.gatra.clan.decorator.BuffDecorator;
import id.ac.ui.cs.advprog.gatra.clan.decorator.DebuffDecorator;
import id.ac.ui.cs.advprog.gatra.clan.decorator.ScoreCalculator;
import id.ac.ui.cs.advprog.gatra.clan.helper.MissionCompletionChecker;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.gatra.scoring.service.ClanScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuffDebuffServiceImpl implements BuffDebuffService {

    private static final double NO_MEMBERS_RATE = 0.0;

    private final ClanMembershipRepository membershipRepository;
    private final MissionCompletionChecker missionCompletionChecker;
    private final ClanScoringService clanScoringService;
    private final QuizAttemptRepository quizAttemptRepository; // ← ini yang replace method gaib

    @Override
    public ScoreCalculator buildCalculator(String clanId) {
        double completionRate = calculateMissionCompletionRate(clanId);
        double avgAccuracy = calculateAverageQuizAccuracy(clanId);

        ScoreCalculator calculator = new BaseScoreCalculator(clanScoringService);

        if (completionRate >= 0.5) {
            calculator = new BuffDecorator(calculator);
        }
        if (avgAccuracy < 0.5) {
            calculator = new DebuffDecorator(calculator);
        }

        return calculator;
    }

    private double calculateMissionCompletionRate(String clanId) {
        List<ClanMembership> members = membershipRepository
                .findByClanIdAndStatus(clanId, MembershipStatus.APPROVED);
        if (members.isEmpty()) return NO_MEMBERS_RATE;

        long completed = members.stream()
                .filter(m -> missionCompletionChecker.hasCompletedAnyMission(m.getUserId()))
                .count();
        return (double) completed / members.size();
    }

    private double calculateAverageQuizAccuracy(String clanId) {
        List<ClanMembership> members = membershipRepository
                .findByClanIdAndStatus(clanId, MembershipStatus.APPROVED);
        if (members.isEmpty()) return 1.0; // no members = no debuff

        double totalAccuracy = members.stream()
                .mapToDouble(m -> getAccuracyForUser(m.getUserId()))
                .average()
                .orElse(1.0);

        return totalAccuracy;
    }

    private double getAccuracyForUser(String userId) {
        List<QuizAttempt> attempts = quizAttemptRepository
                .findByUserId(UUID.fromString(userId));
        if (attempts.isEmpty()) return 1.0;

        double avgScore = attempts.stream()
                .mapToInt(a -> a.getScore() != null ? a.getScore() : 0)
                .average()
                .orElse(100.0);

        return avgScore / 100.0;
    }
}