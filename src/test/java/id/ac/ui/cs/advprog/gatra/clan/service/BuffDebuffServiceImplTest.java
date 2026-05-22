package id.ac.ui.cs.advprog.gatra.clan.service;

import id.ac.ui.cs.advprog.gatra.clan.decorator.ScoreCalculator;
import id.ac.ui.cs.advprog.gatra.clan.helper.MissionCompletionChecker;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.gatra.scoring.service.ClanScoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuffDebuffServiceImplTest {

    @Mock private ClanMembershipRepository membershipRepository;
    @Mock private MissionCompletionChecker missionCompletionChecker;
    @Mock private ClanScoringService clanScoringService;
    @Mock private QuizAttemptRepository quizAttemptRepository;

    private static final String VALID_UUID = "550e8400-e29b-41d4-a716-446655440000"; // ← sini
    private BuffDebuffServiceImpl buffDebuffService;

    @BeforeEach
    void setUp() {
        buffDebuffService = new BuffDebuffServiceImpl(
                membershipRepository, missionCompletionChecker,
                clanScoringService, quizAttemptRepository
        );
    }

    // Helper
    private ClanMembership member(String userId) {
        return ClanMembership.builder()
                .userId(userId)
                .build();
    }

    private QuizAttempt attempt(int score) {
        QuizAttempt a = new QuizAttempt();
        a.setScore(score);
        return a;
    }

    @Test
    void noMembers_noModifier_baseScoreUnchanged() {
        when(membershipRepository.findByClanIdAndStatus(any(), eq(MembershipStatus.APPROVED)))
                .thenReturn(List.of());
        when(clanScoringService.calculateClanScore(any(), any(), any())).thenReturn(100.0);

        ScoreCalculator calc = buffDebuffService.buildCalculator("clan-1");
        assertThat(calc.calculate("clan-1", "BRONZE")).isEqualTo(100.0);
    }

    @Test
    void highCompletion_buffActive_scoreIncreases() {
        // 100% member kelar mission → buff aktif
        when(membershipRepository.findByClanIdAndStatus(any(), eq(MembershipStatus.APPROVED)))
                .thenReturn(List.of(member(VALID_UUID)));
        when(missionCompletionChecker.hasCompletedAnyMission(VALID_UUID)).thenReturn(true);
        when(quizAttemptRepository.findByUserId(any())).thenReturn(List.of());
        when(clanScoringService.calculateClanScore(any(), any(), any())).thenReturn(100.0);

        ScoreCalculator calc = buffDebuffService.buildCalculator("clan-1");
        assertThat(calc.calculate("clan-1", "BRONZE"))
                .isEqualTo(120.0)
                .isGreaterThan(100.0);
    }

    @Test
    void lowAccuracy_debuffActive_scoreDecreases() {
        // quiz score 30 < 50 → debuff aktif
        when(membershipRepository.findByClanIdAndStatus(any(), eq(MembershipStatus.APPROVED)))
                .thenReturn(List.of(member(VALID_UUID)));
        when(missionCompletionChecker.hasCompletedAnyMission(VALID_UUID)).thenReturn(false);
        when(quizAttemptRepository.findByUserId(any())).thenReturn(List.of(attempt(30)));
        when(clanScoringService.calculateClanScore(any(), any(), any())).thenReturn(100.0);

        ScoreCalculator calc = buffDebuffService.buildCalculator("clan-1");
        assertThat(calc.calculate("clan-1", "BRONZE"))
                .isEqualTo(80.0)
                .isLessThan(100.0);
    }

    @Test
    void bothConditions_decoratorsStack() {
        // buff + debuff aktif bersamaan → stackable
        when(membershipRepository.findByClanIdAndStatus(any(), eq(MembershipStatus.APPROVED)))
                .thenReturn(List.of(member(VALID_UUID)));
        when(missionCompletionChecker.hasCompletedAnyMission(VALID_UUID)).thenReturn(true);
        when(quizAttemptRepository.findByUserId(any())).thenReturn(List.of(attempt(30)));
        when(clanScoringService.calculateClanScore(any(), any(), any())).thenReturn(100.0);

        ScoreCalculator calc = buffDebuffService.buildCalculator("clan-1");
        assertThat(calc.calculate("clan-1", "BRONZE")).isEqualTo(96.0); // 100 × 1.2 × 0.8
    }
}