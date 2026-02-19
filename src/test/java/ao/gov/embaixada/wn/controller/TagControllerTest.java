package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.wn.dto.TagCreateRequest;
import ao.gov.embaixada.wn.dto.TagResponse;
import ao.gov.embaixada.wn.service.TagService;
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

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagService tagService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TagService tagService() {
            return mock(TagService.class);
        }
    }

    private TagResponse sampleTag() {
        return new TagResponse(UUID.randomUUID(), "diplomacia",
                "Diplomacia", "Diplomacy", "Diplomatie");
    }

    @Test
    void shouldCreateTag() throws Exception {
        when(tagService.create(any())).thenReturn(sampleTag());

        TagCreateRequest request = new TagCreateRequest("diplomacia", "Diplomacia", "Diplomacy", "Diplomatie");

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("diplomacia"))
                .andExpect(jsonPath("$.data.nomePt").value("Diplomacia"));
    }

    @Test
    void shouldGetTagById() throws Exception {
        TagResponse response = sampleTag();
        when(tagService.findById(response.id())).thenReturn(response);

        mockMvc.perform(get("/api/v1/tags/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slug").value("diplomacia"));
    }

    @Test
    void shouldListTags() throws Exception {
        when(tagService.findAll()).thenReturn(List.of(sampleTag()));

        mockMvc.perform(get("/api/v1/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].slug").value("diplomacia"))
                .andExpect(jsonPath("$.data[0].nomeEn").value("Diplomacy"));
    }

    @Test
    void shouldUpdateTag() throws Exception {
        UUID id = UUID.randomUUID();
        TagResponse updated = new TagResponse(id, "economia", "Economia", "Economy", "Wirtschaft");
        when(tagService.update(eq(id), any())).thenReturn(updated);

        TagCreateRequest request = new TagCreateRequest("economia", "Economia", "Economy", "Wirtschaft");

        mockMvc.perform(put("/api/v1/tags/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slug").value("economia"));
    }

    @Test
    void shouldDeleteTag() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(tagService).delete(id);

        mockMvc.perform(delete("/api/v1/tags/{id}", id))
                .andExpect(status().isNoContent());

        verify(tagService).delete(id);
    }

    @Test
    void shouldRejectCreateWithBlankSlug() throws Exception {
        TagCreateRequest request = new TagCreateRequest("", "Name", null, null);

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithBlankName() throws Exception {
        TagCreateRequest request = new TagCreateRequest("valid-slug", "", null, null);

        mockMvc.perform(post("/api/v1/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
