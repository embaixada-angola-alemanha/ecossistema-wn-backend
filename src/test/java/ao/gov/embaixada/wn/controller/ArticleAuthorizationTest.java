package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.wn.dto.ArticleCreateRequest;
import ao.gov.embaixada.wn.dto.ArticleResponse;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import ao.gov.embaixada.wn.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
class ArticleAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleService articleService;

    @TestConfiguration
    @EnableMethodSecurity
    static class TestConfig {
        @Bean
        public ArticleService articleService() {
            return mock(ArticleService.class);
        }
    }

    private ArticleResponse sampleResponse() {
        return new ArticleResponse(UUID.randomUUID(), "test",
                "Title", null, null, null, null, null, null, null,
                null, null, null, null, null, null,
                EstadoArtigo.DRAFT, null, null, Set.of(), null, false,
                null, null, 0, Instant.now(), Instant.now());
    }

    private ArticleCreateRequest sampleRequest() {
        return new ArticleCreateRequest(
                "test-slug", "Title", null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, false);
    }

    @Test
    void wnEditorCanCreateArticle() throws Exception {
        when(articleService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/articles")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_WN-EDITOR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void wnJournalistCanCreateArticle() throws Exception {
        when(articleService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/articles")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_WN-JOURNALIST")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void wnAdminCanCreateArticle() throws Exception {
        when(articleService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/articles")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_WN-ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void wnJournalistCannotUpdateEstado() throws Exception {
        mockMvc.perform(patch("/api/v1/articles/{id}/estado", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_WN-JOURNALIST")))
                        .param("estado", "PUBLISHED"))
                .andExpect(status().isForbidden());
    }

    @Test
    void wnEditorCanUpdateEstado() throws Exception {
        when(articleService.updateEstado(any(), eq(EstadoArtigo.PUBLISHED))).thenReturn(sampleResponse());

        mockMvc.perform(patch("/api/v1/articles/{id}/estado", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_WN-EDITOR")))
                        .param("estado", "PUBLISHED"))
                .andExpect(status().isOk());
    }

    @Test
    void wnJournalistCannotDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/articles/{id}", UUID.randomUUID())
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_WN-JOURNALIST"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedGets401() throws Exception {
        mockMvc.perform(get("/api/v1/articles"))
                .andExpect(status().isUnauthorized());
    }
}
