package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.wn.dto.AuthorCreateRequest;
import ao.gov.embaixada.wn.dto.AuthorResponse;
import ao.gov.embaixada.wn.service.AuthorService;
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

@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorService authorService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthorService authorService() {
            return mock(AuthorService.class);
        }
    }

    private AuthorResponse sampleAuthor() {
        return new AuthorResponse(UUID.randomUUID(), "Joao Silva", "joao-silva",
                "Jornalista angolano", "Angolan journalist", "Angolanischer Journalist",
                "joao@email.com", null, "JOURNALIST", true);
    }

    private AuthorCreateRequest sampleRequest() {
        return new AuthorCreateRequest(
                "Joao Silva", "joao-silva",
                "Jornalista angolano", "Angolan journalist", "Angolanischer Journalist",
                "joao@email.com", null, null, "JOURNALIST");
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        when(authorService.create(any())).thenReturn(sampleAuthor());

        mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Joao Silva"))
                .andExpect(jsonPath("$.data.slug").value("joao-silva"));
    }

    @Test
    void shouldGetAuthorById() throws Exception {
        AuthorResponse response = sampleAuthor();
        when(authorService.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/authors/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Joao Silva"));
    }

    @Test
    void shouldListAuthors() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(sampleAuthor()));

        mockMvc.perform(get("/api/v1/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nome").value("Joao Silva"));
    }

    @Test
    void shouldListActiveAuthors() throws Exception {
        when(authorService.findActive()).thenReturn(List.of(sampleAuthor()));

        mockMvc.perform(get("/api/v1/authors/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].activo").value(true));
    }

    @Test
    void shouldFindByKeycloakId() throws Exception {
        AuthorResponse response = sampleAuthor();
        when(authorService.findByKeycloakId("kc-123")).thenReturn(response);

        mockMvc.perform(get("/api/v1/authors/keycloak/{keycloakId}", "kc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Joao Silva"));
    }

    @Test
    void shouldUpdateAuthor() throws Exception {
        UUID id = UUID.randomUUID();
        AuthorResponse updated = new AuthorResponse(id, "Joao Updated", "joao-updated",
                null, null, null, "joao@new.com", null, "EDITOR", true);
        when(authorService.update(eq(id), any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/authors/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Joao Updated"));
    }

    @Test
    void shouldToggleActive() throws Exception {
        UUID id = UUID.randomUUID();
        AuthorResponse toggled = new AuthorResponse(id, "Joao Silva", "joao-silva",
                null, null, null, null, null, "JOURNALIST", false);
        when(authorService.toggleActive(id)).thenReturn(toggled);

        mockMvc.perform(patch("/api/v1/authors/{id}/toggle-active", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activo").value(false));
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(authorService).delete(id);

        mockMvc.perform(delete("/api/v1/authors/{id}", id))
                .andExpect(status().isNoContent());

        verify(authorService).delete(id);
    }

    @Test
    void shouldRejectCreateWithBlankName() throws Exception {
        AuthorCreateRequest request = new AuthorCreateRequest(
                "", null, null, null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
