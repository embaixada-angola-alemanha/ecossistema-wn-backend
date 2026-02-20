package ao.gov.embaixada.wn.repository;

import ao.gov.embaixada.wn.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Category> findByActivoOrderBySortOrderAsc(boolean activo);

    @Query("SELECT c FROM Category c LEFT JOIN Article a ON a.category = c " +
           "WHERE c.activo = true GROUP BY c ORDER BY COUNT(a) DESC")
    List<Category> findActiveOrderByArticleCountDesc();
}
