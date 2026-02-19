package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.ArticleCreateRequest;
import ao.gov.embaixada.wn.dto.ArticleResponse;
import ao.gov.embaixada.wn.entity.Article;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import ao.gov.embaixada.wn.exception.DuplicateResourceException;
import ao.gov.embaixada.wn.exception.InvalidStateTransitionException;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.ArticleRepository;
import ao.gov.embaixada.wn.repository.AuthorRepository;
import ao.gov.embaixada.wn.repository.CategoryRepository;
import ao.gov.embaixada.wn.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepo;

    @Mock
    private CategoryRepository categoryRepo;

    @Mock
    private AuthorRepository authorRepo;

    @Mock
    private TagRepository tagRepo;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TagService tagService;

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private ArticleService articleService;

    private Article createArticleEntity(String slug, EstadoArtigo estado) {
        Article a = new Article();
        a.setId(UUID.randomUUID());
        a.setSlug(slug);
        a.setTituloPt("Titulo Teste");
        a.setTituloEn("Test Title");
        a.setEstado(estado);
        a.setTags(new HashSet<>());
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());
        return a;
    }

    private ArticleCreateRequest sampleRequest() {
        return new ArticleCreateRequest(
                "test-article", "Titulo Teste", "Test Title", null, null,
                "<p>Conteudo</p>", "<p>Content</p>", null, null,
                "Resumo", "Summary", null,
                null, null, null,
                null, null, null, null, false);
    }

    @Test
    void shouldCreateArticle() {
        Article entity = createArticleEntity("test-article", EstadoArtigo.DRAFT);
        when(articleRepo.existsBySlug("test-article")).thenReturn(false);
        when(articleRepo.save(any(Article.class))).thenReturn(entity);

        ArticleResponse response = articleService.create(sampleRequest());

        assertNotNull(response);
        assertEquals("test-article", response.slug());
        assertEquals(EstadoArtigo.DRAFT, response.estado());
        verify(articleRepo).save(any(Article.class));
    }

    @Test
    void shouldThrowDuplicateSlug() {
        when(articleRepo.existsBySlug("existing")).thenReturn(true);

        ArticleCreateRequest request = new ArticleCreateRequest(
                "existing", "Title", null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, false);

        assertThrows(DuplicateResourceException.class, () -> articleService.create(request));
    }

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("found", EstadoArtigo.PUBLISHED);
        entity.setId(id);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));

        ArticleResponse response = articleService.findById(id);

        assertEquals("found", response.slug());
    }

    @Test
    void shouldThrowNotFoundById() {
        UUID id = UUID.randomUUID();
        when(articleRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> articleService.findById(id));
    }

    @Test
    void shouldFindBySlug() {
        Article entity = createArticleEntity("slug-test", EstadoArtigo.PUBLISHED);
        when(articleRepo.findBySlug("slug-test")).thenReturn(Optional.of(entity));

        ArticleResponse response = articleService.findBySlug("slug-test");

        assertEquals("slug-test", response.slug());
    }

    @Test
    void shouldFindAllPaged() {
        Article entity = createArticleEntity("listed", EstadoArtigo.DRAFT);
        Page<Article> page = new PageImpl<>(List.of(entity));
        when(articleRepo.findAll(any(Pageable.class))).thenReturn(page);

        Page<ArticleResponse> result = articleService.findAll(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindByEstado() {
        Article entity = createArticleEntity("published", EstadoArtigo.PUBLISHED);
        Page<Article> page = new PageImpl<>(List.of(entity));
        when(articleRepo.findByEstadoOrderByPublishedAtDesc(EstadoArtigo.PUBLISHED, Pageable.unpaged()))
                .thenReturn(page);

        Page<ArticleResponse> result = articleService.findByEstado(EstadoArtigo.PUBLISHED, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldUpdateArticle() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("old-slug", EstadoArtigo.DRAFT);
        entity.setId(id);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));
        when(articleRepo.existsBySlug("test-article")).thenReturn(false);
        when(articleRepo.save(any(Article.class))).thenReturn(entity);

        ArticleResponse response = articleService.update(id, sampleRequest());

        assertNotNull(response);
        verify(articleRepo).save(any(Article.class));
    }

    @Test
    void shouldThrowDuplicateSlugOnUpdate() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("current-slug", EstadoArtigo.DRAFT);
        entity.setId(id);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));
        when(articleRepo.existsBySlug("test-article")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> articleService.update(id, sampleRequest()));
    }

    @Test
    void shouldUpdateEstadoDraftToSubmitted() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("article", EstadoArtigo.DRAFT);
        entity.setId(id);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));
        when(articleRepo.save(any(Article.class))).thenReturn(entity);

        ArticleResponse response = articleService.updateEstado(id, EstadoArtigo.SUBMITTED);

        assertNotNull(response);
    }

    @Test
    void shouldUpdateEstadoSubmittedToInReview() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("article", EstadoArtigo.SUBMITTED);
        entity.setId(id);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));
        when(articleRepo.save(any(Article.class))).thenReturn(entity);

        ArticleResponse response = articleService.updateEstado(id, EstadoArtigo.IN_REVIEW);

        assertNotNull(response);
    }

    @Test
    void shouldUpdateEstadoInReviewToPublished() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("article", EstadoArtigo.IN_REVIEW);
        entity.setId(id);
        entity.setPublishedAt(null);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));
        when(articleRepo.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        articleService.updateEstado(id, EstadoArtigo.PUBLISHED);

        assertNotNull(entity.getPublishedAt());
    }

    @Test
    void shouldRejectInvalidTransitionDraftToPublished() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("article", EstadoArtigo.DRAFT);
        entity.setId(id);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(InvalidStateTransitionException.class,
                () -> articleService.updateEstado(id, EstadoArtigo.PUBLISHED));
    }

    @Test
    void shouldRejectInvalidTransitionDraftToInReview() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("article", EstadoArtigo.DRAFT);
        entity.setId(id);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(InvalidStateTransitionException.class,
                () -> articleService.updateEstado(id, EstadoArtigo.IN_REVIEW));
    }

    @Test
    void shouldRejectInvalidTransitionArchivedToPublished() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("article", EstadoArtigo.ARCHIVED);
        entity.setId(id);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(InvalidStateTransitionException.class,
                () -> articleService.updateEstado(id, EstadoArtigo.PUBLISHED));
    }

    @Test
    void shouldAllowArchivedToDraft() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("article", EstadoArtigo.ARCHIVED);
        entity.setId(id);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));
        when(articleRepo.save(any(Article.class))).thenReturn(entity);

        ArticleResponse response = articleService.updateEstado(id, EstadoArtigo.DRAFT);

        assertNotNull(response);
    }

    @Test
    void shouldSchedulePublication() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("article", EstadoArtigo.DRAFT);
        entity.setId(id);
        Instant scheduledAt = Instant.parse("2026-04-01T10:00:00Z");
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));
        when(articleRepo.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        articleService.schedulePublication(id, scheduledAt);

        assertEquals(scheduledAt, entity.getScheduledAt());
    }

    @Test
    void shouldCancelSchedule() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("article", EstadoArtigo.DRAFT);
        entity.setId(id);
        entity.setScheduledAt(Instant.now());
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));
        when(articleRepo.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        articleService.cancelSchedule(id);

        assertNull(entity.getScheduledAt());
    }

    @Test
    void shouldDeleteArticle() {
        UUID id = UUID.randomUUID();
        when(articleRepo.existsById(id)).thenReturn(true);
        doNothing().when(articleRepo).deleteById(id);

        articleService.delete(id);

        verify(articleRepo).deleteById(id);
    }

    @Test
    void shouldThrowNotFoundOnDelete() {
        UUID id = UUID.randomUUID();
        when(articleRepo.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> articleService.delete(id));
    }

    @Test
    void shouldIncrementViewCount() {
        UUID id = UUID.randomUUID();
        Article entity = createArticleEntity("article", EstadoArtigo.PUBLISHED);
        entity.setId(id);
        entity.setViewCount(10);
        when(articleRepo.findById(id)).thenReturn(Optional.of(entity));
        when(articleRepo.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        articleService.incrementViewCount(id);

        assertEquals(11, entity.getViewCount());
    }
}
