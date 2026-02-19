package ao.gov.embaixada.wn.repository;

import ao.gov.embaixada.wn.entity.ArticleVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleVersionRepository extends JpaRepository<ArticleVersion, UUID> {

    List<ArticleVersion> findByArticleIdOrderByVersionDesc(UUID articleId);

    Optional<ArticleVersion> findByArticleIdAndVersion(UUID articleId, int version);

    int countByArticleId(UUID articleId);
}
