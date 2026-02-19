package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.ArticleCreateRequest;
import ao.gov.embaixada.wn.dto.ArticleResponse;
import ao.gov.embaixada.wn.entity.Article;
import ao.gov.embaixada.wn.entity.Author;
import ao.gov.embaixada.wn.entity.Category;
import ao.gov.embaixada.wn.entity.Tag;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import ao.gov.embaixada.wn.exception.DuplicateResourceException;
import ao.gov.embaixada.wn.exception.InvalidStateTransitionException;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.ArticleRepository;
import ao.gov.embaixada.wn.repository.AuthorRepository;
import ao.gov.embaixada.wn.repository.CategoryRepository;
import ao.gov.embaixada.wn.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepo;
    private final CategoryRepository categoryRepo;
    private final AuthorRepository authorRepo;
    private final TagRepository tagRepo;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final AuthorService authorService;

    public ArticleService(ArticleRepository articleRepo,
                          CategoryRepository categoryRepo,
                          AuthorRepository authorRepo,
                          TagRepository tagRepo,
                          CategoryService categoryService,
                          TagService tagService,
                          AuthorService authorService) {
        this.articleRepo = articleRepo;
        this.categoryRepo = categoryRepo;
        this.authorRepo = authorRepo;
        this.tagRepo = tagRepo;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.authorService = authorService;
    }

    public ArticleResponse create(ArticleCreateRequest req) {
        if (articleRepo.existsBySlug(req.slug())) {
            throw new DuplicateResourceException("Article", "slug", req.slug());
        }
        Article a = new Article();
        mapRequest(a, req);
        return toResponse(articleRepo.save(a));
    }

    @Transactional(readOnly = true)
    public ArticleResponse findById(UUID id) {
        return toResponse(articleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id)));
    }

    @Transactional(readOnly = true)
    public ArticleResponse findBySlug(String slug) {
        return toResponse(articleRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + slug)));
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> findAll(Pageable pageable) {
        return articleRepo.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> findByEstado(EstadoArtigo estado, Pageable pageable) {
        return articleRepo.findByEstadoOrderByPublishedAtDesc(estado, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> findPublished(Pageable pageable) {
        return findByEstado(EstadoArtigo.PUBLISHED, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> findByCategory(UUID categoryId, Pageable pageable) {
        return articleRepo.findByEstadoAndCategory_IdOrderByPublishedAtDesc(
                EstadoArtigo.PUBLISHED, categoryId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> findByAuthor(UUID authorId, Pageable pageable) {
        return articleRepo.findByEstadoAndAuthor_IdOrderByPublishedAtDesc(
                EstadoArtigo.PUBLISHED, authorId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> findByTag(UUID tagId, Pageable pageable) {
        return articleRepo.findByTagAndEstado(tagId, EstadoArtigo.PUBLISHED, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> findFeatured() {
        return articleRepo.findByFeaturedTrueAndEstadoOrderByPublishedAtDesc(EstadoArtigo.PUBLISHED)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> search(String query, Pageable pageable) {
        return articleRepo.search(query, EstadoArtigo.PUBLISHED, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> findEditorial(Pageable pageable) {
        return articleRepo.findByEstadoInOrderByCreatedAtDesc(
                List.of(EstadoArtigo.DRAFT, EstadoArtigo.SUBMITTED, EstadoArtigo.IN_REVIEW),
                pageable).map(this::toResponse);
    }

    public ArticleResponse update(UUID id, ArticleCreateRequest req) {
        Article a = articleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        if (!a.getSlug().equals(req.slug()) && articleRepo.existsBySlug(req.slug())) {
            throw new DuplicateResourceException("Article", "slug", req.slug());
        }
        mapRequest(a, req);
        return toResponse(articleRepo.save(a));
    }

    public ArticleResponse updateEstado(UUID id, EstadoArtigo novoEstado) {
        Article a = articleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        validateTransition(a.getEstado(), novoEstado);
        a.setEstado(novoEstado);
        if (novoEstado == EstadoArtigo.PUBLISHED && a.getPublishedAt() == null) {
            a.setPublishedAt(Instant.now());
        }
        return toResponse(articleRepo.save(a));
    }

    public ArticleResponse schedulePublication(UUID id, Instant scheduledAt) {
        Article a = articleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        a.setScheduledAt(scheduledAt);
        return toResponse(articleRepo.save(a));
    }

    public ArticleResponse cancelSchedule(UUID id) {
        Article a = articleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        a.setScheduledAt(null);
        return toResponse(articleRepo.save(a));
    }

    public void incrementViewCount(UUID id) {
        Article a = articleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        a.setViewCount(a.getViewCount() + 1);
        articleRepo.save(a);
    }

    public void delete(UUID id) {
        if (!articleRepo.existsById(id)) {
            throw new ResourceNotFoundException("Article", id);
        }
        articleRepo.deleteById(id);
    }

    private void validateTransition(EstadoArtigo from, EstadoArtigo to) {
        Map<EstadoArtigo, Set<EstadoArtigo>> allowed = Map.of(
                EstadoArtigo.DRAFT, Set.of(EstadoArtigo.SUBMITTED, EstadoArtigo.ARCHIVED),
                EstadoArtigo.SUBMITTED, Set.of(EstadoArtigo.DRAFT, EstadoArtigo.IN_REVIEW, EstadoArtigo.ARCHIVED),
                EstadoArtigo.IN_REVIEW, Set.of(EstadoArtigo.DRAFT, EstadoArtigo.PUBLISHED, EstadoArtigo.ARCHIVED),
                EstadoArtigo.PUBLISHED, Set.of(EstadoArtigo.DRAFT, EstadoArtigo.ARCHIVED),
                EstadoArtigo.ARCHIVED, Set.of(EstadoArtigo.DRAFT)
        );
        if (!allowed.getOrDefault(from, Set.of()).contains(to)) {
            throw new InvalidStateTransitionException(from.name(), to.name());
        }
    }

    private void mapRequest(Article a, ArticleCreateRequest req) {
        a.setSlug(req.slug());
        a.setTituloPt(req.tituloPt());
        a.setTituloEn(req.tituloEn());
        a.setTituloDe(req.tituloDe());
        a.setTituloCs(req.tituloCs());
        a.setConteudoPt(req.conteudoPt());
        a.setConteudoEn(req.conteudoEn());
        a.setConteudoDe(req.conteudoDe());
        a.setConteudoCs(req.conteudoCs());
        a.setExcertoPt(req.excertoPt());
        a.setExcertoEn(req.excertoEn());
        a.setExcertoDe(req.excertoDe());
        a.setMetaTituloPt(req.metaTituloPt());
        a.setMetaDescricaoPt(req.metaDescricaoPt());
        a.setMetaKeywords(req.metaKeywords());
        a.setFeaturedImageId(req.featuredImageId());
        a.setFeatured(req.featured());

        if (req.categoryId() != null) {
            Category cat = categoryRepo.findById(req.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", req.categoryId()));
            a.setCategory(cat);
        } else {
            a.setCategory(null);
        }

        if (req.authorId() != null) {
            Author author = authorRepo.findById(req.authorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author", req.authorId()));
            a.setAuthor(author);
        } else {
            a.setAuthor(null);
        }

        if (req.tagIds() != null && !req.tagIds().isEmpty()) {
            Set<Tag> tags = tagRepo.findByIdIn(req.tagIds());
            a.setTags(tags);
        } else {
            a.setTags(new HashSet<>());
        }
    }

    ArticleResponse toResponse(Article a) {
        return new ArticleResponse(
                a.getId(), a.getSlug(),
                a.getTituloPt(), a.getTituloEn(), a.getTituloDe(), a.getTituloCs(),
                a.getConteudoPt(), a.getConteudoEn(), a.getConteudoDe(), a.getConteudoCs(),
                a.getExcertoPt(), a.getExcertoEn(), a.getExcertoDe(),
                a.getMetaTituloPt(), a.getMetaDescricaoPt(), a.getMetaKeywords(),
                a.getEstado(),
                a.getCategory() != null ? categoryService.toResponse(a.getCategory()) : null,
                a.getAuthor() != null ? authorService.toResponse(a.getAuthor()) : null,
                a.getTags().stream().map(t -> tagService.toResponse(t)).collect(Collectors.toSet()),
                a.getFeaturedImageId(), a.isFeatured(),
                a.getPublishedAt(), a.getScheduledAt(),
                a.getViewCount(),
                a.getCreatedAt(), a.getUpdatedAt()
        );
    }
}
