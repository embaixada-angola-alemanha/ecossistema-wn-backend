package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.CategoryCreateRequest;
import ao.gov.embaixada.wn.dto.CategoryResponse;
import ao.gov.embaixada.wn.entity.Category;
import ao.gov.embaixada.wn.exception.DuplicateResourceException;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repo;

    @InjectMocks
    private CategoryService categoryService;

    private Category createEntity(String slug) {
        Category c = new Category();
        c.setId(UUID.randomUUID());
        c.setSlug(slug);
        c.setNomePt("Politica");
        c.setNomeEn("Politics");
        c.setNomeDe("Politik");
        c.setCor("#FF0000");
        c.setSortOrder(1);
        c.setActivo(true);
        c.setCreatedAt(Instant.now());
        c.setUpdatedAt(Instant.now());
        return c;
    }

    @Test
    void shouldCreateCategory() {
        Category entity = createEntity("politica");
        when(repo.existsBySlug("politica")).thenReturn(false);
        when(repo.save(any(Category.class))).thenReturn(entity);

        CategoryCreateRequest request = new CategoryCreateRequest(
                "politica", "Politica", "Politics", "Politik", null,
                null, null, null, "#FF0000", 1);

        CategoryResponse response = categoryService.create(request);

        assertNotNull(response);
        assertEquals("politica", response.slug());
        assertEquals("Politica", response.nomePt());
        assertTrue(response.activo());
        verify(repo).save(any(Category.class));
    }

    @Test
    void shouldThrowDuplicateSlug() {
        when(repo.existsBySlug("existing")).thenReturn(true);

        CategoryCreateRequest request = new CategoryCreateRequest(
                "existing", "Name", null, null, null, null, null, null, null, null);

        assertThrows(DuplicateResourceException.class, () -> categoryService.create(request));
    }

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        Category entity = createEntity("test");
        entity.setId(id);
        when(repo.findById(id)).thenReturn(Optional.of(entity));

        CategoryResponse response = categoryService.findById(id);

        assertEquals("test", response.slug());
    }

    @Test
    void shouldThrowNotFoundById() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.findById(id));
    }

    @Test
    void shouldFindBySlug() {
        Category entity = createEntity("economia");
        when(repo.findBySlug("economia")).thenReturn(Optional.of(entity));

        CategoryResponse response = categoryService.findBySlug("economia");

        assertEquals("economia", response.slug());
    }

    @Test
    void shouldFindAll() {
        when(repo.findAll(any(Sort.class))).thenReturn(List.of(createEntity("cat1"), createEntity("cat2")));

        List<CategoryResponse> result = categoryService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldFindActive() {
        when(repo.findActiveOrderByArticleCountDesc()).thenReturn(List.of(createEntity("active")));

        List<CategoryResponse> result = categoryService.findActive();

        assertEquals(1, result.size());
    }

    @Test
    void shouldUpdateCategory() {
        UUID id = UUID.randomUUID();
        Category entity = createEntity("old-slug");
        entity.setId(id);
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(repo.existsBySlug("new-slug")).thenReturn(false);
        when(repo.save(any(Category.class))).thenReturn(entity);

        CategoryCreateRequest request = new CategoryCreateRequest(
                "new-slug", "New Name", null, null, null, null, null, null, null, null);

        CategoryResponse response = categoryService.update(id, request);

        assertNotNull(response);
        verify(repo).save(any(Category.class));
    }

    @Test
    void shouldThrowDuplicateSlugOnUpdate() {
        UUID id = UUID.randomUUID();
        Category entity = createEntity("current");
        entity.setId(id);
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(repo.existsBySlug("taken")).thenReturn(true);

        CategoryCreateRequest request = new CategoryCreateRequest(
                "taken", "Name", null, null, null, null, null, null, null, null);

        assertThrows(DuplicateResourceException.class, () -> categoryService.update(id, request));
    }

    @Test
    void shouldToggleActive() {
        UUID id = UUID.randomUUID();
        Category entity = createEntity("toggle-test");
        entity.setId(id);
        entity.setActivo(true);
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(repo.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        categoryService.toggleActive(id);

        assertFalse(entity.isActivo());
    }

    @Test
    void shouldDeleteCategory() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(true);
        doNothing().when(repo).deleteById(id);

        categoryService.delete(id);

        verify(repo).deleteById(id);
    }

    @Test
    void shouldThrowNotFoundOnDelete() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.delete(id));
    }
}
