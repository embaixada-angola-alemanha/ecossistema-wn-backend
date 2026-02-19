package ao.gov.embaixada.wn.integration;

import ao.gov.embaixada.commons.integration.event.Exchanges;
import ao.gov.embaixada.commons.integration.event.IntegrationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes SGC activity events for news article creation.
 * WN↔SGC: When the consulate completes significant activities
 * (visa milestones, new services, events), WN can create draft articles.
 */
@Component
public class WnSgcConsumer {

    private static final Logger log = LoggerFactory.getLogger(WnSgcConsumer.class);

    @RabbitListener(queues = Exchanges.QUEUE_WN_FROM_SGC, concurrency = "1-3")
    public void handleSgcEvent(IntegrationEvent event) {
        log.info("WN received SGC event: type={}, entity={}, entityId={}",
            event.eventType(), event.entityType(), event.entityId());

        switch (event.eventType()) {
            case "SGC_ACTIVITY_COMPLETED" -> handleActivityForNews(event);
            case "SGC_VISTO_STATE_CHANGED" -> handleVistoStateForNews(event);
            default -> log.debug("WN ignoring SGC event type: {}", event.eventType());
        }
    }

    /**
     * When SGC completes a significant activity, create a draft news article
     * for the editorial team to review and publish.
     */
    private void handleActivityForNews(IntegrationEvent event) {
        String description = (String) event.payload().getOrDefault("description", "");
        String entityType = event.entityType();
        log.info("SGC activity for news: entityType={}, description={} — creating draft article suggestion", entityType, description);
        // TODO: Create DRAFT article with auto-generated content from activity
        // ArticleService.createFromActivity(entityType, description, event.payload())
    }

    /**
     * Track significant visa milestones for potential news (e.g. 1000th visa issued).
     */
    private void handleVistoStateForNews(IntegrationEvent event) {
        String newState = (String) event.payload().getOrDefault("newState", "");
        if ("EMITIDO".equals(newState)) {
            log.info("Visa issued: {} — checking milestones for news", event.entityId());
            // TODO: Check if this triggers a milestone (e.g. 100th, 500th, 1000th)
        }
    }
}
