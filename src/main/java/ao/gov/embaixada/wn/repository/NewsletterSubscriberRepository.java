package ao.gov.embaixada.wn.repository;

import ao.gov.embaixada.wn.entity.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, UUID> {

    Optional<NewsletterSubscriber> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<NewsletterSubscriber> findByConfirmationToken(String token);

    List<NewsletterSubscriber> findByActivoTrueAndConfirmedTrue();

    List<NewsletterSubscriber> findByActivoTrueAndConfirmedTrueAndIdioma(String idioma);
}
