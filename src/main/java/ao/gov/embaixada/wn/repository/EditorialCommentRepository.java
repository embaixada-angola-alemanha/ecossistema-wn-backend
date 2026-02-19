package ao.gov.embaixada.wn.repository;

import ao.gov.embaixada.wn.entity.EditorialComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EditorialCommentRepository extends JpaRepository<EditorialComment, UUID> {

    List<EditorialComment> findByArticleIdOrderByCreatedAtAsc(UUID articleId);

    List<EditorialComment> findByArticleIdAndResolvedFalseOrderByCreatedAtAsc(UUID articleId);

    int countByArticleIdAndResolvedFalse(UUID articleId);
}
