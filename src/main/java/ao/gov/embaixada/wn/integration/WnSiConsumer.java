package ao.gov.embaixada.wn.integration;

import ao.gov.embaixada.commons.integration.event.Exchanges;
import ao.gov.embaixada.commons.integration.event.IntegrationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes SI events for news creation.
 * WN↔SI: When the institutional site publishes events,
 * WN can auto-create coverage draft articles.
 */
@Component
public class WnSiConsumer {

    private static final Logger log = LoggerFactory.getLogger(WnSiConsumer.class);

    @RabbitListener(queues = Exchanges.QUEUE_WN_FROM_SI, concurrency = "1-2")
    public void handleSiEvent(IntegrationEvent event) {
        log.info("WN received SI event: type={}, entity={}, entityId={}",
            event.eventType(), event.entityType(), event.entityId());

        switch (event.eventType()) {
            case "SI_EVENT_PUBLISHED" -> handleEventPublished(event);
            case "SI_EVENT_CANCELLED" -> handleEventCancelled(event);
            default -> log.debug("WN ignoring SI event type: {}", event.eventType());
        }
    }

    /**
     * When SI publishes an institutional event, create a draft article
     * for the news team to cover the event.
     */
    private void handleEventPublished(IntegrationEvent event) {
        String tituloPt = (String) event.payload().getOrDefault("tituloPt", "");
        String dataInicio = (String) event.payload().getOrDefault("dataInicio", "");
        String slug = (String) event.payload().getOrDefault("slug", "");
        log.info("SI event published: '{}' on {} — creating draft article for news coverage", tituloPt, dataInicio);
        // TODO: ArticleService.createFromSiEvent(tituloPt, dataInicio, slug, event.payload())
    }

    /**
     * When SI cancels an event, flag any related draft articles.
     */
    private void handleEventCancelled(IntegrationEvent event) {
        String slug = (String) event.payload().getOrDefault("slug", "");
        log.info("SI event cancelled: slug={} — flagging related draft articles", slug);
        // TODO: ArticleService.flagRelatedDrafts(slug, "SI event cancelled")
    }
}
