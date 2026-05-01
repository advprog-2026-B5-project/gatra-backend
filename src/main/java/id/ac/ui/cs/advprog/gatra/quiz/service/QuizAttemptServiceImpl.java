package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.clan.model.MembershipStatus;
import id.ac.ui.cs.advprog.gatra.clan.repository.ClanMembershipRepository;
import id.ac.ui.cs.advprog.gatra.dto.MilestoneResponse;
import id.ac.ui.cs.advprog.gatra.model.ActionType;
import id.ac.ui.cs.advprog.gatra.model.Article;
import id.ac.ui.cs.advprog.gatra.repository.ArticleRepository;
import id.ac.ui.cs.advprog.gatra.quiz.dto.QuizResultResponse;
import id.ac.ui.cs.advprog.gatra.quiz.dto.SubmitQuizRequest;
import id.ac.ui.cs.advprog.gatra.quiz.model.*;
import id.ac.ui.cs.advprog.gatra.quiz.repository.*;
import id.ac.ui.cs.advprog.gatra.scoring.model.PointActivityType;
import id.ac.ui.cs.advprog.gatra.scoring.service.PointRecordingService;
import id.ac.ui.cs.advprog.gatra.service.MilestoneService;
import id.ac.ui.cs.advprog.gatra.service.MissionProgressService;
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

    @Override
    @Transactional
    public QuizResultResponse submitQuiz(SubmitQuizRequest request) {
        // ambil artikel untuk passing score
        Article article = articleRepository.findById(request.getArticleId()).orElseThrow(() -> new RuntimeException("Article not found"));

        // ambil semua soal artikel ini
        List<Question> questions = questionRepository.findByArticleId(request.getArticleId());

        List<QuizAnswer> quizAnswers = new ArrayList<>();
        int correct = 0;

        for (SubmitQuizRequest.AnswerItem item : request.getAnswers()) {
            Question question = questions.stream()
                    .filter(q -> q.getId().equals(item.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            boolean isCorrect = checkAnswer(question, item.getAnswer());
            if (isCorrect) correct++;

            QuizAnswer quizAnswer = new QuizAnswer();
            quizAnswer.setQuestionId(question.getId());
            quizAnswer.setUserAnswer(item.getAnswer());
            quizAnswer.setIsCorrect(isCorrect);
            quizAnswers.add(quizAnswer);
        }

        float score = questions.isEmpty() ? 0 :
                ((float) correct / questions.size()) * 100;

        float passingScore = article.getPassingScore();
        boolean passed = score >= passingScore;

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUserId(request.getUserId());
        attempt.setArticleId(request.getArticleId());
        attempt.setScore(Math.round(score));
        attempt.setPassed(passed);

        // hubungkan answer ke attempt
        quizAnswers.forEach(a -> a.setAttempt(attempt));
        attempt.setAnswers(quizAnswers);

        attemptRepository.save(attempt);

        QuizResultResponse response = new QuizResultResponse(score, passingScore, passed, quizAnswers);

        response.setPointsEarned(0.0);

        if (passed) {
            MilestoneResponse milestoneResponse = milestoneService.recordAction(
                    request.getUserId(), ActionType.FINISH_QUIZ);
            var completedMissions = missionProgressService.incrementProgress(request.getUserId(), "FINISH_QUIZ");
            milestoneResponse.setCompletedMissions(completedMissions);
            response.setMilestoneResponse(milestoneResponse);

            // Extract Optional check to set pointsEarned safely
            var membershipOpt = clanMembershipRepository.findFirstByUserIdAndStatus(request.getUserId().toString(), MembershipStatus.APPROVED);

            if (membershipOpt.isPresent()) {
                pointRecordingService.recordPoints(
                        request.getUserId().toString(),
                        membershipOpt.get().getClan().getId(),
                        100.0, // Passing gets 100 points.
                        PointActivityType.QUIZ_PASSED,
                        request.getArticleId().toString()
                );
                response.setPointsEarned(100.0);
            }
        }

        return response;
    }

    @Override
    public boolean hasUserPassed(UUID userId, UUID articleId) {
        return attemptRepository.existsByUserIdAndArticleIdAndPassedTrue(userId, articleId);
    }

    private boolean checkAnswer(Question question, String userAnswer) {
        if (question instanceof MultipleChoiceQuestion mcq) {
            return mcq.getCorrectAnswer().equalsIgnoreCase(userAnswer);
        } else if (question instanceof TrueFalseQuestion tfq) {
            return tfq.getCorrectAnswer().equalsIgnoreCase(userAnswer);
        }
        return false;
    }
}
