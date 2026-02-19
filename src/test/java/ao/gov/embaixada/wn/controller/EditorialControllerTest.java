package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.wn.dto.*;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import ao.gov.embaixada.wn.service.ArticleService;
import ao.gov.embaixada.wn.service.ArticleVersionService;
import ao.gov.embaixada.wn.service.EditorialCommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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

@WebMvcTest(EditorialController.class)
@AutoConfigureMockMvc(addFilters = false)
class EditorialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleVersionService versionService;

    @Autowired
    private EditorialCommentService commentService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ArticleService articleService() {
            return mock(ArticleService.class);
        }

        @Bean
        public ArticleVersionService versionService() {
            return mock(ArticleVersionService.class);
        }

        @Bean
        public EditorialCommentService commentService() {
            return mock(EditorialCommentService.class);
        }
    }

    private ArticleResponse sampleArticle(EstadoArtigo estado) {
        return new ArticleResponse(UUID.randomUUID(), "test",
                "Title", null, null, null, null, null, null, null,
                null, null, null, null, null, null,
                estado, null, null, Set.of(), null, false,
                null, null, 0, Instant.now(), Instant.now());
    }

    // --- Workflow tests ---

    @Test
    void shouldSubmitArticle() throws Exception {
        UUID id = UUID.randomUUID();
        ArticleVersionResponse versionResp = new ArticleVersionResponse(
                UUID.randomUUID(), id, 1L, "Title", "Content", null, null,
                null, "Submitted for review", "workflow", Instant.now());
        when(versionService.createVersion(eq(id), any(), any())).thenReturn(versionResp);
        when(articleService.updateEstado(id, EstadoArtigo.SUBMITTED))
                .thenReturn(sampleArticle(EstadoArtigo.SUBMITTED));

        mockMvc.perform(patch("/api/v1/editorial/articles/{id}/submit", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("SUBMITTED"));
    }

    @Test
    void shouldStartReview() throws Exception {
        UUID id = UUID.randomUUID();
        when(articleService.updateEstado(id, EstadoArtigo.IN_REVIEW))
                .thenReturn(sampleArticle(EstadoArtigo.IN_REVIEW));

        mockMvc.perform(patch("/api/v1/editorial/articles/{id}/review", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("IN_REVIEW"));
    }

    @Test
    void shouldPublishArticle() throws Exception {
        UUID id = UUID.randomUUID();
        ArticleVersionResponse versionResp = new ArticleVersionResponse(
                UUID.randomUUID(), id, 2L, "Title", "Content", null, null,
                null, "Published", "workflow", Instant.now());
        when(versionService.createVersion(eq(id), any(), any())).thenReturn(versionResp);
        when(articleService.updateEstado(id, EstadoArtigo.PUBLISHED))
                .thenReturn(sampleArticle(EstadoArtigo.PUBLISHED));

        mockMvc.perform(patch("/api/v1/editorial/articles/{id}/publish", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("PUBLISHED"));
    }

    @Test
    void shouldRejectArticle() throws Exception {
        UUID id = UUID.randomUUID();
        when(articleService.updateEstado(id, EstadoArtigo.DRAFT))
                .thenReturn(sampleArticle(EstadoArtigo.DRAFT));

        mockMvc.perform(patch("/api/v1/editorial/articles/{id}/reject", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("DRAFT"));
    }

    @Test
    void shouldArchiveArticle() throws Exception {
        UUID id = UUID.randomUUID();
        when(articleService.updateEstado(id, EstadoArtigo.ARCHIVED))
                .thenReturn(sampleArticle(EstadoArtigo.ARCHIVED));

        mockMvc.perform(patch("/api/v1/editorial/articles/{id}/archive", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado").value("ARCHIVED"));
    }

    // --- Scheduling tests ---

    @Test
    void shouldScheduleArticle() throws Exception {
        UUID id = UUID.randomUUID();
        when(articleService.schedulePublication(eq(id), any(Instant.class)))
                .thenReturn(sampleArticle(EstadoArtigo.DRAFT));

        mockMvc.perform(patch("/api/v1/editorial/articles/{id}/schedule", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"scheduledAt\":\"2026-03-01T10:00:00Z\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCancelSchedule() throws Exception {
        UUID id = UUID.randomUUID();
        when(articleService.cancelSchedule(id)).thenReturn(sampleArticle(EstadoArtigo.DRAFT));

        mockMvc.perform(delete("/api/v1/editorial/articles/{id}/schedule", id))
                .andExpect(status().isOk());
    }

    // --- Versioning tests ---

    @Test
    void shouldListVersions() throws Exception {
        UUID id = UUID.randomUUID();
        ArticleVersionResponse version = new ArticleVersionResponse(
                UUID.randomUUID(), id, 1L, "Title", "Content", null, null,
                null, "Initial", "admin", Instant.now());
        when(versionService.findByArticle(id)).thenReturn(List.of(version));

        mockMvc.perform(get("/api/v1/editorial/articles/{id}/versions", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].version").value(1));
    }

    @Test
    void shouldGetVersion() throws Exception {
        UUID id = UUID.randomUUID();
        ArticleVersionResponse version = new ArticleVersionResponse(
                UUID.randomUUID(), id, 2L, "Title V2", "Content V2", null, null,
                null, "Updated", "editor", Instant.now());
        when(versionService.findByArticleAndVersion(id, 2)).thenReturn(version);

        mockMvc.perform(get("/api/v1/editorial/articles/{id}/versions/{version}", id, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.version").value(2));
    }

    @Test
    void shouldCreateVersion() throws Exception {
        UUID id = UUID.randomUUID();
        ArticleVersionResponse version = new ArticleVersionResponse(
                UUID.randomUUID(), id, 3L, "Title", "Content", null, null,
                null, "Manual save", "admin", Instant.now());
        when(versionService.createVersion(eq(id), any(), any())).thenReturn(version);

        mockMvc.perform(post("/api/v1/editorial/articles/{id}/versions", id)
                        .param("summary", "Manual save")
                        .param("createdBy", "admin"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.version").value(3));
    }

    @Test
    void shouldRestoreVersion() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(versionService).restoreVersion(id, 1);

        mockMvc.perform(post("/api/v1/editorial/articles/{id}/versions/{version}/restore", id, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Restored to version 1"));
    }

    // --- Comment tests ---

    @Test
    void shouldListComments() throws Exception {
        UUID id = UUID.randomUUID();
        CommentResponse comment = new CommentResponse(UUID.randomUUID(), id,
                UUID.randomUUID(), "Editor", "Good article", "review", null,
                false, Instant.now(), Instant.now());
        when(commentService.findByArticle(id)).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/v1/editorial/articles/{id}/comments", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].conteudo").value("Good article"));
    }

    @Test
    void shouldListUnresolvedComments() throws Exception {
        UUID id = UUID.randomUUID();
        CommentResponse comment = new CommentResponse(UUID.randomUUID(), id,
                UUID.randomUUID(), "Reviewer", "Fix typo", "correction", null,
                false, Instant.now(), Instant.now());
        when(commentService.findUnresolved(id)).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/v1/editorial/articles/{id}/comments/unresolved", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].resolved").value(false));
    }

    @Test
    void shouldAddComment() throws Exception {
        UUID articleId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        CommentResponse comment = new CommentResponse(UUID.randomUUID(), articleId,
                authorId, "Editor", "Needs revision", "review", null,
                false, Instant.now(), Instant.now());
        when(commentService.addComment(eq(articleId), any())).thenReturn(comment);

        CommentCreateRequest request = new CommentCreateRequest(authorId, "Needs revision", "review", null);

        mockMvc.perform(post("/api/v1/editorial/articles/{id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.conteudo").value("Needs revision"));
    }

    @Test
    void shouldResolveComment() throws Exception {
        UUID commentId = UUID.randomUUID();
        CommentResponse resolved = new CommentResponse(commentId, UUID.randomUUID(),
                UUID.randomUUID(), "Editor", "Fixed", "review", null,
                true, Instant.now(), Instant.now());
        when(commentService.resolveComment(commentId)).thenReturn(resolved);

        mockMvc.perform(patch("/api/v1/editorial/comments/{commentId}/resolve", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.resolved").value(true));
    }

    @Test
    void shouldDeleteComment() throws Exception {
        UUID commentId = UUID.randomUUID();
        doNothing().when(commentService).deleteComment(commentId);

        mockMvc.perform(delete("/api/v1/editorial/comments/{commentId}", commentId))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(commentId);
    }
}
