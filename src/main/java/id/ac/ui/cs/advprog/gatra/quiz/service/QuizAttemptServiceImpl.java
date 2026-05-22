package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.achievement.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.achievement.model.ActionType;
import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.article.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.gatra.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.gatra.quiz.dto.SubmitQuizRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.*;
import id.ac.ui.cs.advprog.gatra.quiz.repository.*;
import id.ac.ui.cs.advprog.gatra.achievement.service.MilestoneService;
import id.ac.ui.cs.advprog.gatra.achievement.service.MissionProgressService;
import id.ac.ui.cs.advprog.gatra.scoring.model.PointActivityType;
import id.ac.ui.cs.advprog.gatra.scoring.service.PointRecordingService;
import id.ac.ui.cs.advprog.gatra.quiz.monitoring.MonitoringQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizAttemptRepository attemptRepository;
    private final QuestionRepository questionRepository;
    private final ArticleRepository articleRepository;
    private final MilestoneService milestoneService;
    private final MissionProgressService missionProgressService;
    private final ClanMembershipRepository clanMembershipRepository;
    private final PointRecordingService pointRecordingService;
    private final MonitoringQuestion monitoringQuestion;

    @Override
    @Transactional
    public QuizResultResponse submitQuiz(SubmitQuizRequest request) {
        if (hasUserPassed(request.getUserId(), request.getArticleId())) {
            throw new IllegalStateException("Sudah lulus kuis ini");
        }
        // ambil artikel untuk passing score
        Article article = articleRepository.findById(request.getArticleId()).orElseThrow(() -> new ResourceNotFoundException("Article", request.getArticleId()));

        // ambil semua soal artikel ini
        List<Question> questions = questionRepository.findByArticleId(request.getArticleId());

        List<QuizAnswer> quizAnswers = buildAnswers(request, questions);
        int correct = (int) quizAnswers.stream().filter(QuizAnswer::getIsCorrect).count();


        float score = questions.isEmpty() ? 0 :
                ((float) correct / questions.size()) * 100;

        float passingScore = article.getPassingScore();
        boolean passed = score >= passingScore;

        QuizAttempt attempt = buildAttempt(request, score, passed, quizAnswers);
        attemptRepository.save(attempt);

        monitoringQuestion.incrementQuizSubmitted();
        if (passed) {
            monitoringQuestion.incrementQuizPassed();
        } else {
            monitoringQuestion.incrementQuizFailed();
        }

        QuizResultResponse response = new QuizResultResponse(score, passingScore, passed, quizAnswers);

        response.setPointsEarned(0.0);

        if (passed) {
            handlePassedQuiz(request, response);
        }

        return response;
    }

    @Override
    public boolean hasUserPassed(UUID userId, UUID articleId) {
        return attemptRepository.existsByUserIdAndArticleIdAndPassedTrue(userId, articleId);
    }

    private boolean checkAnswer(Question question, String userAnswer) {
        return question.checkAnswer(userAnswer);
    }

    private List<QuizAnswer> buildAnswers(SubmitQuizRequest request, List<Question> questions) {
        List<QuizAnswer> quizAnswers = new ArrayList<>();

        for (SubmitQuizRequest.AnswerItem item : request.getAnswers()) {
            Question question = questions.stream()
                    .filter(q -> q.getId().equals(item.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Question", item.getQuestionId()));

            QuizAnswer quizAnswer = new QuizAnswer();
            quizAnswer.setQuestionId(question.getId());
            quizAnswer.setUserAnswer(item.getAnswer());
            quizAnswer.setIsCorrect(checkAnswer(question,item.getAnswer()));
            quizAnswers.add(quizAnswer);
        }
        return quizAnswers;
    }

    private QuizAttempt buildAttempt(SubmitQuizRequest request, float score,
                                     boolean passed, List<QuizAnswer> quizAnswers) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUserId(request.getUserId());
        attempt.setArticleId(request.getArticleId());
        attempt.setScore(Math.round(score));
        attempt.setPassed(passed);
        quizAnswers.forEach(a -> a.setAttempt(attempt));
        attempt.setAnswers(quizAnswers);
        return attempt;
    }

    private void handlePassedQuiz(SubmitQuizRequest request, QuizResultResponse response) {
        MilestoneResponse milestoneResponse = milestoneService.recordAction(request.getUserId(), ActionType.FINISH_QUIZ);
        var completedMissions = missionProgressService.incrementProgress(request.getUserId(), "FINISH_QUIZ");
        milestoneResponse.setCompletedMissions(completedMissions);
        response.setMilestoneResponse(milestoneResponse);

        clanMembershipRepository
                .findFirstByUserIdAndStatus(request.getUserId().toString(), MembershipStatus.APPROVED)
                .ifPresent(membership -> {
                    pointRecordingService.recordPoints(
                            request.getUserId().toString(),
                            membership.getClan().getId(),
                            100.0,
                            PointActivityType.QUIZ_PASSED,
                            request.getArticleId().toString()
                    );
                    response.setPointsEarned(100.0);
                });
    }
}
