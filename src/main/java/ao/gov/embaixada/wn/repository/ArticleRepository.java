package ao.gov.embaixada.wn.repository;

import ao.gov.embaixada.wn.entity.Article;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {

    Optional<Article> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Article> findByEstadoOrderByPublishedAtDesc(EstadoArtigo estado, Pageable pageable);

    Page<Article> findByEstadoAndCategory_IdOrderByPublishedAtDesc(
            EstadoArtigo estado, UUID categoryId, Pageable pageable);

    Page<Article> findByEstadoAndAuthor_IdOrderByPublishedAtDesc(
            EstadoArtigo estado, UUID authorId, Pageable pageable);

    List<Article> findByFeaturedTrueAndEstadoOrderByPublishedAtDesc(EstadoArtigo estado);

    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.id = :tagId AND a.estado = :estado ORDER BY a.publishedAt DESC")
    Page<Article> findByTagAndEstado(@Param("tagId") UUID tagId, @Param("estado") EstadoArtigo estado, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.estado = :estado AND " +
            "(LOWER(a.tituloPt) LIKE LOWER(CONCAT('%',:query,'%')) OR " +
            " LOWER(a.tituloEn) LIKE LOWER(CONCAT('%',:query,'%')) OR " +
            " LOWER(a.conteudoPt) LIKE LOWER(CONCAT('%',:query,'%')))")
    Page<Article> search(@Param("query") String query, @Param("estado") EstadoArtigo estado, Pageable pageable);

    List<Article> findByScheduledAtBeforeAndEstado(Instant now, EstadoArtigo estado);

    Page<Article> findByEstadoInOrderByCreatedAtDesc(List<EstadoArtigo> estados, Pageable pageable);
}
