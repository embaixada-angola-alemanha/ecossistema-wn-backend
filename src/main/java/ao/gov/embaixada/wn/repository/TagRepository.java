package ao.gov.embaixada.wn.repository;

import ao.gov.embaixada.wn.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    Optional<Tag> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Set<Tag> findByIdIn(Set<UUID> ids);
}
