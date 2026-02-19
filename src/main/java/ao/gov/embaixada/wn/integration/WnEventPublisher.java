package ao.gov.embaixada.wn.integration;

import ao.gov.embaixada.commons.integration.IntegrationEventPublisher;
import ao.gov.embaixada.commons.integration.event.EventTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Publishes WN events to the cross-system integration exchange.
 * Consumed by GPJ (monitoring) and potentially SI (news feed widget).
 */
@Service
public class WnEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(WnEventPublisher.class);

    private final IntegrationEventPublisher publisher;

    public WnEventPublisher(@Nullable IntegrationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void articlePublished(String articleId, String slug, String tituloPt, String categoryName) {
        if (publisher == null) return;
        publisher.publish(EventTypes.SOURCE_WN, EventTypes.WN_ARTICLE_PUBLISHED, articleId, "Article",
            Map.of("slug", slug, "tituloPt", tituloPt, "category", categoryName));
        log.info("Published WN article event: slug={}", slug);
    }

    public void articleArchived(String articleId, String slug) {
        if (publisher == null) return;
        publisher.publish(EventTypes.SOURCE_WN, EventTypes.WN_ARTICLE_ARCHIVED, articleId, "Article",
            Map.of("slug", slug));
    }

    public void newsletterSent(String newsletterId, int recipientCount) {
        if (publisher == null) return;
        publisher.publish(EventTypes.SOURCE_WN, EventTypes.WN_NEWSLETTER_SENT, newsletterId, "Newsletter",
            Map.of("recipientCount", recipientCount));
    }
}
