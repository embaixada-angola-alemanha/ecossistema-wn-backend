package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.TagCreateRequest;
import ao.gov.embaixada.wn.dto.TagResponse;
import ao.gov.embaixada.wn.entity.Tag;
import ao.gov.embaixada.wn.exception.DuplicateResourceException;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.TagRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class TagService {

    private final TagRepository repo;

    public TagService(TagRepository repo) {
        this.repo = repo;
    }

    public TagResponse create(TagCreateRequest req) {
        if (repo.existsBySlug(req.slug())) {
            throw new DuplicateResourceException("Tag", "slug", req.slug());
        }
        Tag t = new Tag();
        mapRequest(t, req);
        return toResponse(repo.save(t));
    }

    @Transactional(readOnly = true)
    public TagResponse findById(UUID id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id)));
    }

    @Transactional(readOnly = true)
    public List<TagResponse> findAll() {
        return repo.findAll(Sort.by("nomePt")).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Set<Tag> findByIds(Set<UUID> ids) {
        return repo.findByIdIn(ids);
    }

    public TagResponse update(UUID id, TagCreateRequest req) {
        Tag t = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
        if (!t.getSlug().equals(req.slug()) && repo.existsBySlug(req.slug())) {
            throw new DuplicateResourceException("Tag", "slug", req.slug());
        }
        mapRequest(t, req);
        return toResponse(repo.save(t));
    }

    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Tag", id);
        }
        repo.deleteById(id);
    }

    private void mapRequest(Tag t, TagCreateRequest req) {
        t.setSlug(req.slug());
        t.setNomePt(req.nomePt());
        t.setNomeEn(req.nomeEn());
        t.setNomeDe(req.nomeDe());
    }

    TagResponse toResponse(Tag t) {
        return new TagResponse(
                t.getId(), t.getSlug(),
                t.getNomePt(), t.getNomeEn(), t.getNomeDe()
        );
    }
}
