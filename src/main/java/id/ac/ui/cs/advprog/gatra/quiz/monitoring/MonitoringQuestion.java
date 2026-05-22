package id.ac.ui.cs.advprog.gatra.quiz.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MonitoringQuestion {

    private final Counter questionCreatedCounter;
    private final Counter questionUpdatedCounter;
    private final Counter questionDeletedCounter;
    private final Counter passingScoreUpdatedCounter;

    private final Counter quizSubmittedCounter;
    private final Counter quizPassedCounter;
    private final Counter quizFailedCounter;

    public MonitoringQuestion(MeterRegistry meterRegistry) {
        this.questionCreatedCounter = Counter.builder("monitoring_question_created_total")
                .description("Total number of quiz questions successfully created")
                .register(meterRegistry);

        this.questionUpdatedCounter = Counter.builder("monitoring_question_updated_total")
                .description("Total number of quiz questions successfully updated")
                .register(meterRegistry);

        this.questionDeletedCounter = Counter.builder("monitoring_question_deleted_total")
                .description("Total number of quiz questions successfully deleted")
                .register(meterRegistry);

        this.passingScoreUpdatedCounter = Counter.builder("monitoring_passing_score_updated_total")
                .description("Total number of quiz passing score updates")
                .register(meterRegistry);

        this.quizSubmittedCounter = Counter.builder("monitoring_quiz_submitted_total")
                .description("Total number of quiz attempts submitted")
                .register(meterRegistry);

        this.quizPassedCounter = Counter.builder("monitoring_quiz_passed_total")
                .description("Total number of quiz attempts passed")
                .register(meterRegistry);

        this.quizFailedCounter = Counter.builder("monitoring_quiz_failed_total")
                .description("Total number of quiz attempts failed")
                .register(meterRegistry);
    }

    public void incrementQuestionCreated() {
        questionCreatedCounter.increment();
    }

    public void incrementQuestionUpdated() {
        questionUpdatedCounter.increment();
    }

    public void incrementQuestionDeleted() {
        questionDeletedCounter.increment();
    }

    public void incrementPassingScoreUpdated() {
        passingScoreUpdatedCounter.increment();
    }

    public void incrementQuizSubmitted() {
        quizSubmittedCounter.increment();
    }

    public void incrementQuizPassed() {
        quizPassedCounter.increment();
    }

    public void incrementQuizFailed() {
        quizFailedCounter.increment();
    }
}