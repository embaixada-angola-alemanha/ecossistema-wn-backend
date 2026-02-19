package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.NewsletterSubscribeRequest;
import ao.gov.embaixada.wn.dto.NewsletterSubscriberResponse;
import ao.gov.embaixada.wn.entity.NewsletterSubscriber;
import ao.gov.embaixada.wn.exception.DuplicateResourceException;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.NewsletterSubscriberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NewsletterService {

    private final NewsletterSubscriberRepository repo;

    public NewsletterService(NewsletterSubscriberRepository repo) {
        this.repo = repo;
    }

    public NewsletterSubscriberResponse subscribe(NewsletterSubscribeRequest req) {
        if (repo.existsByEmail(req.email())) {
            throw new DuplicateResourceException("Subscriber", "email", req.email());
        }
        NewsletterSubscriber s = new NewsletterSubscriber();
        s.setEmail(req.email());
        s.setNome(req.nome());
        s.setIdioma(req.idioma() != null ? req.idioma() : "pt");
        s.setConfirmationToken(UUID.randomUUID().toString());
        s.setConfirmed(false);
        return toResponse(repo.save(s));
    }

    public NewsletterSubscriberResponse confirm(String token) {
        NewsletterSubscriber s = repo.findByConfirmationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid confirmation token"));
        s.setConfirmed(true);
        s.setConfirmationToken(null);
        return toResponse(repo.save(s));
    }

    public void unsubscribe(String email) {
        NewsletterSubscriber s = repo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found: " + email));
        s.setActivo(false);
        repo.save(s);
    }

    @Transactional(readOnly = true)
    public List<NewsletterSubscriberResponse> findActiveSubscribers() {
        return repo.findByActivoTrueAndConfirmedTrue().stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NewsletterSubscriberResponse> findByIdioma(String idioma) {
        return repo.findByActivoTrueAndConfirmedTrueAndIdioma(idioma).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public long countActiveSubscribers() {
        return repo.findByActivoTrueAndConfirmedTrue().size();
    }

    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Subscriber", id);
        }
        repo.deleteById(id);
    }

    private NewsletterSubscriberResponse toResponse(NewsletterSubscriber s) {
        return new NewsletterSubscriberResponse(
                s.getId(), s.getEmail(), s.getNome(),
                s.getIdioma(), s.isActivo(), s.isConfirmed(),
                s.getCreatedAt()
        );
    }
}
