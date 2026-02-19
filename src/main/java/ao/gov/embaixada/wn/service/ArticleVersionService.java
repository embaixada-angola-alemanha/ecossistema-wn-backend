package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.ArticleVersionResponse;
import ao.gov.embaixada.wn.entity.Article;
import ao.gov.embaixada.wn.entity.ArticleVersion;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.ArticleRepository;
import ao.gov.embaixada.wn.repository.ArticleVersionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ArticleVersionService {

    private static final Logger log = LoggerFactory.getLogger(ArticleVersionService.class);

    private final ArticleVersionRepository versionRepo;
    private final ArticleRepository articleRepo;
    private final ObjectMapper objectMapper;

    public ArticleVersionService(ArticleVersionRepository versionRepo,
                                  ArticleRepository articleRepo,
                                  ObjectMapper objectMapper) {
        this.versionRepo = versionRepo;
        this.articleRepo = articleRepo;
        this.objectMapper = objectMapper;
    }

    public ArticleVersionResponse createVersion(UUID articleId, String changeSummary, String createdBy) {
        Article article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        long nextVersion = versionRepo.countByArticleId(articleId) + 1L;

        ArticleVersion v = new ArticleVersion();
        v.setArticleId(articleId);
        v.setVersion(nextVersion);
        v.setTituloPt(article.getTituloPt());
        v.setConteudoPt(article.getConteudoPt());
        v.setConteudoEn(article.getConteudoEn());
        v.setConteudoDe(article.getConteudoDe());
        v.setExcertoPt(article.getExcertoPt());
        v.setChangeSummary(changeSummary);
        v.setCreatedBy(createdBy);

        try {
            String snapshot = objectMapper.writeValueAsString(Map.of(
                    "tituloPt", nullSafe(article.getTituloPt()),
                    "tituloEn", nullSafe(article.getTituloEn()),
                    "tituloDe", nullSafe(article.getTituloDe()),
                    "conteudoPt", nullSafe(article.getConteudoPt()),
                    "conteudoEn", nullSafe(article.getConteudoEn()),
                    "conteudoDe", nullSafe(article.getConteudoDe()),
                    "excertoPt", nullSafe(article.getExcertoPt()),
                    "estado", article.getEstado().name(),
                    "slug", article.getSlug()
            ));
            v.setSnapshot(snapshot);
        } catch (Exception e) {
            log.warn("Failed to serialize snapshot for article {}: {}", articleId, e.getMessage());
        }

        return toResponse(versionRepo.save(v));
    }

    @Transactional(readOnly = true)
    public List<ArticleVersionResponse> findByArticle(UUID articleId) {
        return versionRepo.findByArticleIdOrderByVersionDesc(articleId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ArticleVersionResponse findByArticleAndVersion(UUID articleId, int version) {
        return toResponse(versionRepo.findByArticleIdAndVersion(articleId, version)
                .orElseThrow(() -> new ResourceNotFoundException("ArticleVersion not found: v" + version)));
    }

    public void restoreVersion(UUID articleId, int version) {
        ArticleVersion v = versionRepo.findByArticleIdAndVersion(articleId, version)
                .orElseThrow(() -> new ResourceNotFoundException("ArticleVersion not found: v" + version));
        Article article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        createVersion(articleId, "Auto-saved before restore to v" + version, "system");

        article.setTituloPt(v.getTituloPt());
        article.setConteudoPt(v.getConteudoPt());
        article.setConteudoEn(v.getConteudoEn());
        article.setConteudoDe(v.getConteudoDe());
        article.setExcertoPt(v.getExcertoPt());
        articleRepo.save(article);
    }

    private String nullSafe(String val) {
        return val != null ? val : "";
    }

    private ArticleVersionResponse toResponse(ArticleVersion v) {
        return new ArticleVersionResponse(
                v.getId(), v.getArticleId(), v.getVersion(),
                v.getTituloPt(), v.getConteudoPt(), v.getConteudoEn(), v.getConteudoDe(),
                v.getExcertoPt(), v.getChangeSummary(), v.getCreatedBy(),
                v.getCreatedAt()
        );
    }
}
