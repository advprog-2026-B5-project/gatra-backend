package id.ac.ui.cs.advprog.gatra.article.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MonitoringArticle {

    private final Counter articleCreatedCounter;
    private final Counter articleViewedCounter;
    private final Counter articleMarkedReadCounter;
    private final Counter articleDeletedCounter;

    public MonitoringArticle(MeterRegistry meterRegistry) {
        this.articleCreatedCounter = Counter.builder("monitoring_article_created_total")
                .description("Total number of articles successfully created")
                .register(meterRegistry);

        this.articleViewedCounter = Counter.builder("monitoring_article_viewed_total")
                .description("Total number of articles successfully viewed by id")
                .register(meterRegistry);

        this.articleMarkedReadCounter = Counter.builder("monitoring_article_marked_read_total")
                .description("Total number of articles marked as read")
                .register(meterRegistry);

        this.articleDeletedCounter = Counter.builder("monitoring_article_deleted_total")
                .description("Total number of articles successfully soft deleted")
                .register(meterRegistry);
    }

    public void incrementArticleCreated() {
        articleCreatedCounter.increment();
    }

    public void incrementArticleViewed() {
        articleViewedCounter.increment();
    }

    public void incrementArticleMarkedRead() {
        articleMarkedReadCounter.increment();
    }

    public void incrementArticleDeleted() {
        articleDeletedCounter.increment();
    }
}