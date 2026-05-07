package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.achievement.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.model.Article;
import id.ac.ui.cs.advprog.gatra.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.gatra.quiz.dto.SubmitQuizRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.MultipleChoiceQuestion;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuestionRepository;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.gatra.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.scoring.model.PointActivityType;
import id.ac.ui.cs.advprog.gatra.scoring.service.PointRecordingService;
import id.ac.ui.cs.advprog.gatra.achievement.service.MilestoneService;
import id.ac.ui.cs.advprog.gatra.achievement.service.MissionProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizAttemptScoringIntegrationTest {

    @Mock private QuizAttemptRepository attemptRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private ArticleRepository articleRepository;
    @Mock private MilestoneService milestoneService;
    @Mock private MissionProgressService missionProgressService;

    // Core mocks for this integration test
    @Mock private ClanMembershipRepository clanMembershipRepository;
    @Mock private PointRecordingService pointRecordingService;

    @InjectMocks
    private QuizAttemptServiceImpl quizAttemptService;

    private SubmitQuizRequest request;
    private UUID userId;
    private UUID articleId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        articleId = UUID.randomUUID();

        request = new SubmitQuizRequest();
        request.setUserId(userId);
        request.setArticleId(articleId);

        // Set up a passing quiz scenario (1 Question, user answers correctly)
        UUID questionId = UUID.randomUUID();
        SubmitQuizRequest.AnswerItem answerItem = new SubmitQuizRequest.AnswerItem();
        answerItem.setQuestionId(questionId);
        answerItem.setAnswer("A");
        request.setAnswers(List.of(answerItem));

        Article mockArticle = new Article();
        mockArticle.setId(articleId);
        mockArticle.setPassingScore(100);

        MultipleChoiceQuestion mockQuestion = new MultipleChoiceQuestion();
        mockQuestion.setId(questionId);
        mockQuestion.setCorrectAnswer("A");

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(mockArticle));
        when(questionRepository.findByArticleId(articleId)).thenReturn(List.of(mockQuestion));

        // Mock the personal milestone/mission updates so they don't throw null pointers
        when(milestoneService.recordAction(any(), any())).thenReturn(new MilestoneResponse());
        when(missionProgressService.incrementProgress(any(), anyString())).thenReturn(List.of());
    }

    @Test
    void submitQuiz_UserPassesAndIsInClan_RecordsPoints() {
        // Arrange
        Clan mockClan = new Clan();
        mockClan.setId("clan-123");

        ClanMembership mockMembership = new ClanMembership();
        mockMembership.setClan(mockClan);

        // User is in an approved clan
        when(clanMembershipRepository.findFirstByUserIdAndStatus(userId.toString(), MembershipStatus.APPROVED))
                .thenReturn(Optional.of(mockMembership));

        // Act
        QuizResultResponse response = quizAttemptService.submitQuiz(request);

        // Assert
        assertTrue(response.getPassed());
        verify(pointRecordingService, times(1)).recordPoints(
                userId.toString(),
                "clan-123",
                100.0,
                PointActivityType.QUIZ_PASSED,
                articleId.toString()
        );
    }

    @Test
    void submitQuiz_UserPassesButNotInClan_DoesNotRecordPoints() {
        // Arrange
        // User is NOT in a clan (returns empty)
        when(clanMembershipRepository.findFirstByUserIdAndStatus(userId.toString(), MembershipStatus.APPROVED))
                .thenReturn(Optional.empty());

        // Act
        QuizResultResponse response = quizAttemptService.submitQuiz(request);

        // Assert
        assertTrue(response.getPassed());
        // Verify ledger was never touched
        verify(pointRecordingService, never()).recordPoints(anyString(), anyString(), anyDouble(), any(), anyString());
    }
}