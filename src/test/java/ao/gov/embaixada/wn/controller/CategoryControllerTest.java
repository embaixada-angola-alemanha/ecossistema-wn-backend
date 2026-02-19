package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.wn.dto.CategoryCreateRequest;
import ao.gov.embaixada.wn.dto.CategoryResponse;
import ao.gov.embaixada.wn.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CategoryService categoryService() {
            return mock(CategoryService.class);
        }
    }

    private CategoryResponse sampleCategory() {
        return new CategoryResponse(UUID.randomUUID(), "politica",
                "Politica", "Politics", "Politik", null,
                "Noticias politicas", "Political news", "Politische Nachrichten",
                "#FF0000", 1, true);
    }

    private CategoryCreateRequest sampleRequest() {
        return new CategoryCreateRequest(
                "politica", "Politica", "Politics", "Politik", null,
                "Noticias politicas", "Political news", "Politische Nachrichten",
                "#FF0000", 1);
    }

    @Test
    void shouldCreateCategory() throws Exception {
        when(categoryService.create(any())).thenReturn(sampleCategory());

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("politica"))
                .andExpect(jsonPath("$.data.nomePt").value("Politica"));
    }

    @Test
    void shouldGetCategoryById() throws Exception {
        CategoryResponse response = sampleCategory();
        when(categoryService.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/categories/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slug").value("politica"));
    }

    @Test
    void shouldListCategories() throws Exception {
        when(categoryService.findAll()).thenReturn(List.of(sampleCategory()));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].slug").value("politica"))
                .andExpect(jsonPath("$.data[0].cor").value("#FF0000"));
    }

    @Test
    void shouldUpdateCategory() throws Exception {
        UUID id = UUID.randomUUID();
        CategoryResponse updated = new CategoryResponse(id, "economia",
                "Economia", "Economy", "Wirtschaft", null,
                null, null, null, "#00FF00", 2, true);
        when(categoryService.update(eq(id), any())).thenReturn(updated);

        CategoryCreateRequest request = new CategoryCreateRequest(
                "economia", "Economia", "Economy", "Wirtschaft", null,
                null, null, null, "#00FF00", 2);

        mockMvc.perform(put("/api/v1/categories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slug").value("economia"));
    }

    @Test
    void shouldToggleActive() throws Exception {
        UUID id = UUID.randomUUID();
        CategoryResponse toggled = new CategoryResponse(id, "politica",
                "Politica", null, null, null, null, null, null,
                null, 1, false);
        when(categoryService.toggleActive(id)).thenReturn(toggled);

        mockMvc.perform(patch("/api/v1/categories/{id}/toggle-active", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activo").value(false));
    }

    @Test
    void shouldDeleteCategory() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(categoryService).delete(id);

        mockMvc.perform(delete("/api/v1/categories/{id}", id))
                .andExpect(status().isNoContent());

        verify(categoryService).delete(id);
    }

    @Test
    void shouldRejectCreateWithBlankSlug() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest(
                "", "Name", null, null, null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithBlankName() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest(
                "valid-slug", "", null, null, null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
