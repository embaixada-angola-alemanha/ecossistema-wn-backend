package ao.gov.embaixada.wn.repository;

import ao.gov.embaixada.wn.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {

    Optional<Author> findBySlug(String slug);

    Optional<Author> findByKeycloakId(String keycloakId);

    List<Author> findByActivoOrderByNomeAsc(boolean activo);
}
