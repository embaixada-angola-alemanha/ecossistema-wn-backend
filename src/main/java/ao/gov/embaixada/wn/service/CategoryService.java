package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.CategoryCreateRequest;
import ao.gov.embaixada.wn.dto.CategoryResponse;
import ao.gov.embaixada.wn.entity.Category;
import ao.gov.embaixada.wn.exception.DuplicateResourceException;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public CategoryResponse create(CategoryCreateRequest req) {
        if (repo.existsBySlug(req.slug())) {
            throw new DuplicateResourceException("Category", "slug", req.slug());
        }
        Category c = new Category();
        mapRequest(c, req);
        return toResponse(repo.save(c));
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(UUID id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id)));
    }

    @Transactional(readOnly = true)
    public CategoryResponse findBySlug(String slug) {
        return toResponse(repo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + slug)));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return repo.findAll(Sort.by("sortOrder")).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findActive() {
        return repo.findByActivoOrderBySortOrderAsc(true).stream()
                .map(this::toResponse).toList();
    }

    public CategoryResponse update(UUID id, CategoryCreateRequest req) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        if (!c.getSlug().equals(req.slug()) && repo.existsBySlug(req.slug())) {
            throw new DuplicateResourceException("Category", "slug", req.slug());
        }
        mapRequest(c, req);
        return toResponse(repo.save(c));
    }

    public CategoryResponse toggleActive(UUID id) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        c.setActivo(!c.isActivo());
        return toResponse(repo.save(c));
    }

    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }
        repo.deleteById(id);
    }

    private void mapRequest(Category c, CategoryCreateRequest req) {
        c.setSlug(req.slug());
        c.setNomePt(req.nomePt());
        c.setNomeEn(req.nomeEn());
        c.setNomeDe(req.nomeDe());
        c.setNomeCs(req.nomeCs());
        c.setDescricaoPt(req.descricaoPt());
        c.setDescricaoEn(req.descricaoEn());
        c.setDescricaoDe(req.descricaoDe());
        c.setCor(req.cor());
        c.setSortOrder(req.sortOrder() != null ? req.sortOrder() : 0);
    }

    CategoryResponse toResponse(Category c) {
        return new CategoryResponse(
                c.getId(), c.getSlug(),
                c.getNomePt(), c.getNomeEn(), c.getNomeDe(), c.getNomeCs(),
                c.getDescricaoPt(), c.getDescricaoEn(), c.getDescricaoDe(),
                c.getCor(), c.getSortOrder(), c.isActivo()
        );
    }
}
