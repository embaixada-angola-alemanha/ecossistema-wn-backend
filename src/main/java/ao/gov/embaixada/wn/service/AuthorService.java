package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.AuthorCreateRequest;
import ao.gov.embaixada.wn.dto.AuthorResponse;
import ao.gov.embaixada.wn.entity.Author;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.AuthorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository repo;

    public AuthorService(AuthorRepository repo) {
        this.repo = repo;
    }

    public AuthorResponse create(AuthorCreateRequest req) {
        Author a = new Author();
        mapRequest(a, req);
        return toResponse(repo.save(a));
    }

    @Transactional(readOnly = true)
    public AuthorResponse findById(UUID id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id)));
    }

    @Transactional(readOnly = true)
    public AuthorResponse findBySlug(String slug) {
        return toResponse(repo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + slug)));
    }

    @Transactional(readOnly = true)
    public AuthorResponse findByKeycloakId(String keycloakId) {
        return toResponse(repo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found for keycloak: " + keycloakId)));
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findAll() {
        return repo.findAll(Sort.by("nome")).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> findActive() {
        return repo.findByActivoOrderByNomeAsc(true).stream()
                .map(this::toResponse).toList();
    }

    public AuthorResponse update(UUID id, AuthorCreateRequest req) {
        Author a = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id));
        mapRequest(a, req);
        return toResponse(repo.save(a));
    }

    public AuthorResponse toggleActive(UUID id) {
        Author a = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", id));
        a.setActivo(!a.isActivo());
        return toResponse(repo.save(a));
    }

    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Author", id);
        }
        repo.deleteById(id);
    }

    private void mapRequest(Author a, AuthorCreateRequest req) {
        a.setNome(req.nome());
        a.setSlug(req.slug());
        a.setBioPt(req.bioPt());
        a.setBioEn(req.bioEn());
        a.setBioDe(req.bioDe());
        a.setEmail(req.email());
        a.setAvatarId(req.avatarId());
        a.setKeycloakId(req.keycloakId());
        a.setRole(req.role());
    }

    AuthorResponse toResponse(Author a) {
        return new AuthorResponse(
                a.getId(), a.getNome(), a.getSlug(),
                a.getBioPt(), a.getBioEn(), a.getBioDe(),
                a.getEmail(), a.getAvatarId(), a.getRole(), a.isActivo()
        );
    }
}
