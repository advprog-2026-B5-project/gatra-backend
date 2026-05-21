package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.achievement.service.MilestoneService;
import id.ac.ui.cs.advprog.gatra.achievement.service.MissionProgressService;
import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.article.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.clan.model.Clan;
import id.ac.ui.cs.advprog.gatra.clan.model.ClanMembership;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.gatra.quiz.dto.SubmitQuizRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.MultipleChoiceQuestion;
import id.ac.ui.cs.advprog.gatra.quiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.gatra.quiz.model.TrueFalseQuestion;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuestionRepository;
import id.ac.ui.cs.advprog.gatra.quiz.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.gatra.scoring.model.PointActivityType;
import id.ac.ui.cs.advprog.gatra.scoring.service.PointRecordingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizAttemptServiceImplTest {

    @Mock
    private QuizAttemptRepository attemptRepository;
    
    @Mock
    private QuestionRepository questionRepository;
    
    @Mock
    private ArticleRepository articleRepository;
    
    @Mock
    private MilestoneService milestoneService;
    
    @Mock
    private MissionProgressService missionProgressService;
    
    @Mock
    private ClanMembershipRepository clanMembershipRepository;
    
    @Mock
    private PointRecordingService pointRecordingService;

    @InjectMocks
    private QuizAttemptServiceImpl quizAttemptService;

    private UUID userId;
    private UUID articleId;
    private UUID tfQuestionId;
    private UUID mcqQuestionId;
    private Article dummyArticle;
    private TrueFalseQuestion tfQuestion;
    private MultipleChoiceQuestion mcqQuestion;
    private SubmitQuizRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        articleId = UUID.randomUUID();
        tfQuestionId = UUID.randomUUID();
        mcqQuestionId = UUID.randomUUID();

        dummyArticle = new Article();
        dummyArticle.setId(articleId);
        dummyArticle.setPassingScore(50);

        tfQuestion = new TrueFalseQuestion();
        tfQuestion.setId(tfQuestionId);
        tfQuestion.setText("True False Test");
        tfQuestion.setCorrectAnswer("True");
        tfQuestion.setArticle(dummyArticle);

        mcqQuestion = new MultipleChoiceQuestion();
        mcqQuestion.setId(mcqQuestionId);
        mcqQuestion.setText("MCQ Test");
        mcqQuestion.setCorrectAnswer("A");
        mcqQuestion.setArticle(dummyArticle);

        request = new SubmitQuizRequest();
        request.setUserId(userId);
        request.setArticleId(articleId);
    }

    @Test
    void submitQuiz_shouldCalculateScoreAndPassUser() {
        SubmitQuizRequest.AnswerItem ans1 = new SubmitQuizRequest.AnswerItem();
        ans1.setQuestionId(tfQuestionId);
        ans1.setAnswer("True");

        SubmitQuizRequest.AnswerItem ans2 = new SubmitQuizRequest.AnswerItem();
        ans2.setQuestionId(mcqQuestionId);
        ans2.setAnswer("A");

        request.setAnswers(List.of(ans1, ans2));

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(dummyArticle));
        when(questionRepository.findByArticleId(articleId)).thenReturn(List.of(tfQuestion, mcqQuestion));
        when(attemptRepository.save(any(QuizAttempt.class))).thenReturn(new QuizAttempt());
        
        MilestoneResponse milestoneResponse = new MilestoneResponse();
        when(milestoneService.recordAction(eq(userId), eq(ActionType.FINISH_QUIZ))).thenReturn(milestoneResponse);
        when(missionProgressService.incrementProgress(eq(userId), eq("FINISH_QUIZ"))).thenReturn(null);

        ClanMembership dummyMembership = new ClanMembership();
        Clan dummyClan = new Clan();
        dummyClan.setId(UUID.randomUUID().toString());
        dummyMembership.setClan(dummyClan);
        
        when(clanMembershipRepository.findFirstByUserIdAndStatus(eq(userId.toString()), eq(MembershipStatus.APPROVED)))
                .thenReturn(Optional.of(dummyMembership));

        doNothing().when(pointRecordingService).recordPoints(anyString(), anyString(), anyDouble(), any(PointActivityType.class), anyString());

        QuizResultResponse response = quizAttemptService.submitQuiz(request);

        assertNotNull(response);
        assertEquals(100.0f, response.getScore());
        assertTrue(response.getPassed());
        assertEquals(100.0, response.getPointsEarned());
        
        verify(attemptRepository, times(1)).save(any(QuizAttempt.class));
        verify(pointRecordingService, times(1)).recordPoints(
                eq(userId.toString()),
                eq(dummyClan.getId()),
                eq(100.0),
                eq(PointActivityType.QUIZ_PASSED),
                eq(articleId.toString())
        );
    }

    @Test
    void submitQuiz_shouldFailUserWhenScoreBelowPassing() {
        SubmitQuizRequest.AnswerItem ans1 = new SubmitQuizRequest.AnswerItem();
        ans1.setQuestionId(tfQuestionId);
        ans1.setAnswer("False"); // wrong

        SubmitQuizRequest.AnswerItem ans2 = new SubmitQuizRequest.AnswerItem();
        ans2.setQuestionId(mcqQuestionId);
        ans2.setAnswer("B"); // wrong

        request.setAnswers(List.of(ans1, ans2));

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(dummyArticle));
        when(questionRepository.findByArticleId(articleId)).thenReturn(List.of(tfQuestion, mcqQuestion));
        when(attemptRepository.save(any(QuizAttempt.class))).thenReturn(new QuizAttempt());

        QuizResultResponse response = quizAttemptService.submitQuiz(request);

        assertNotNull(response);
        assertEquals(0.0f, response.getScore());
        assertFalse(response.getPassed());
        assertEquals(0.0, response.getPointsEarned());

        verify(milestoneService, never()).recordAction(any(), any());
        verify(pointRecordingService, never()).recordPoints(anyString(), any(), anyDouble(), any(), anyString());
    }

    @Test
    void submitQuiz_shouldThrowExceptionWhenArticleNotFound() {
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizAttemptService.submitQuiz(request));
    }

    @Test
    void submitQuiz_shouldThrowExceptionWhenQuestionNotFound() {
        SubmitQuizRequest.AnswerItem ans1 = new SubmitQuizRequest.AnswerItem();
        ans1.setQuestionId(UUID.randomUUID()); // non-existent question
        ans1.setAnswer("True");
        request.setAnswers(List.of(ans1));

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(dummyArticle));
        when(questionRepository.findByArticleId(articleId)).thenReturn(List.of(tfQuestion));

        assertThrows(RuntimeException.class, () -> quizAttemptService.submitQuiz(request));
    }

    @Test
    void hasUserPassed_shouldReturnTrueWhenPassed() {
        when(attemptRepository.existsByUserIdAndArticleIdAndPassedTrue(userId, articleId)).thenReturn(true);

        boolean passed = quizAttemptService.hasUserPassed(userId, articleId);

        assertTrue(passed);
    }
}
