package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.entity.Article;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import ao.gov.embaixada.wn.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

    private final ArticleRepository articleRepo;

    public SchedulerService(ArticleRepository articleRepo) {
        this.articleRepo = articleRepo;
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void publishScheduledArticles() {
        List<Article> scheduled = articleRepo.findByScheduledAtBeforeAndEstado(
                Instant.now(), EstadoArtigo.DRAFT);
        for (Article article : scheduled) {
            article.setEstado(EstadoArtigo.PUBLISHED);
            article.setPublishedAt(Instant.now());
            article.setScheduledAt(null);
            articleRepo.save(article);
            log.info("Auto-published scheduled article: {} ({})", article.getSlug(), article.getId());
        }
        if (!scheduled.isEmpty()) {
            log.info("Published {} scheduled articles", scheduled.size());
        }
    }
}
