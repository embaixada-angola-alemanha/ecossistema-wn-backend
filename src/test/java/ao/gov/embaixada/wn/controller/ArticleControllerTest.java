package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.wn.dto.ArticleCreateRequest;
import ao.gov.embaixada.wn.dto.ArticleResponse;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import ao.gov.embaixada.wn.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleService articleService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ArticleService articleService() {
            return mock(ArticleService.class);
        }
    }

    private ArticleResponse sampleArticle() {
        return new ArticleResponse(UUID.randomUUID(), "angola-news-2026",
                "Noticias de Angola", "Angola News", "Angola Nachrichten", null,
                "<p>Conteudo</p>", "<p>Content</p>", "<p>Inhalt</p>", null,
                "Resumo", "Summary", "Zusammenfassung",
                "Meta Title", "Meta Desc", "angola,news",
                EstadoArtigo.DRAFT, null, null, Set.of(), null, false,
                null, null, 0, Instant.now(), Instant.now());
    }

    private ArticleCreateRequest sampleRequest() {
        return new ArticleCreateRequest(
                "angola-news-2026", "Noticias de Angola", "Angola News", "Angola Nachrichten", null,
                "<p>Conteudo</p>", "<p>Content</p>", "<p>Inhalt</p>", null,
                "Resumo", "Summary", "Zusammenfassung",
                "Meta Title", "Meta Desc", "angola,news",
                null, null, null, null, false);
    }

    @Test
    void shouldCreateArticle() throws Exception {
        when(articleService.create(any())).thenReturn(sampleArticle());

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("angola-news-2026"))
                .andExpect(jsonPath("$.data.tituloPt").value("Noticias de Angola"));
    }

    @Test
    void shouldGetArticleById() throws Exception {
        ArticleResponse response = sampleArticle();
        when(articleService.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/articles/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slug").value("angola-news-2026"));
    }

    @Test
    void shouldListArticles() throws Exception {
        Page<ArticleResponse> page = new PageImpl<>(List.of(sampleArticle()));
        when(articleService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].slug").value("angola-news-2026"));
    }

    @Test
    void shouldListArticlesWithEstadoFilter() throws Exception {
        Page<ArticleResponse> page = new PageImpl<>(List.of(sampleArticle()));
        when(articleService.findByEstado(eq(EstadoArtigo.DRAFT), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/articles")
                        .param("estado", "DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].estado").value("DRAFT"));
    }

    @Test
    void shouldListEditorialArticles() throws Exception {
        Page<ArticleResponse> page = new PageImpl<>(List.of(sampleArticle()));
        when(articleService.findEditorial(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/articles/editorial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].slug").value("angola-news-2026"));
    }

    @Test
    void shouldUpdateArticle() throws Exception {
        UUID id = UUID.randomUUID();
        ArticleResponse response = sampleArticle();
        when(articleService.update(eq(id), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/articles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tituloPt").value("Noticias de Angola"));
    }

    @Test
    void shouldUpdateEstado() throws Exception {
        UUID id = UUID.randomUUID();
        ArticleResponse response = new ArticleResponse(id, "test",
                "Title", null, null, null, null, null, null, null,
                null, null, null, null, null, null,
                EstadoArtigo.SUBMITTED, null, null, Set.of(), null, false,
                null, null, 0, Instant.now(), Instant.now());
        when(articleService.updateEstado(eq(id), eq(EstadoArtigo.SUBMITTED))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/articles/{id}/estado", id)
                        .param("estado", "SUBMITTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("SUBMITTED"));
    }

    @Test
    void shouldDeleteArticle() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(articleService).delete(id);

        mockMvc.perform(delete("/api/v1/articles/{id}", id))
                .andExpect(status().isNoContent());

        verify(articleService).delete(id);
    }

    @Test
    void shouldRejectCreateWithBlankSlug() throws Exception {
        ArticleCreateRequest request = new ArticleCreateRequest(
                "", "Title", null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, false);

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithBlankTitle() throws Exception {
        ArticleCreateRequest request = new ArticleCreateRequest(
                "valid-slug", "", null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, false);

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
