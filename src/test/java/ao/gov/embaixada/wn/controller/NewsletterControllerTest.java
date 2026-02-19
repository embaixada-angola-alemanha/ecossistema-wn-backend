package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.wn.dto.NewsletterSubscribeRequest;
import ao.gov.embaixada.wn.dto.NewsletterSubscriberResponse;
import ao.gov.embaixada.wn.service.NewsletterService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsletterController.class)
@AutoConfigureMockMvc(addFilters = false)
class NewsletterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsletterService newsletterService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public NewsletterService newsletterService() {
            return mock(NewsletterService.class);
        }
    }

    private NewsletterSubscriberResponse sampleSubscriber() {
        return new NewsletterSubscriberResponse(UUID.randomUUID(),
                "subscriber@email.com", "Maria Silva", "PT",
                true, true, Instant.now());
    }

    // --- Public endpoints ---

    @Test
    void shouldSubscribe() throws Exception {
        when(newsletterService.subscribe(any())).thenReturn(sampleSubscriber());

        NewsletterSubscribeRequest request = new NewsletterSubscribeRequest(
                "subscriber@email.com", "Maria Silva", "PT");

        mockMvc.perform(post("/api/v1/public/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("subscriber@email.com"));
    }

    @Test
    void shouldConfirmSubscription() throws Exception {
        when(newsletterService.confirm("abc123")).thenReturn(sampleSubscriber());

        mockMvc.perform(get("/api/v1/public/newsletter/confirm")
                        .param("token", "abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Subscription confirmed"));
    }

    @Test
    void shouldUnsubscribe() throws Exception {
        doNothing().when(newsletterService).unsubscribe("user@email.com");

        mockMvc.perform(post("/api/v1/public/newsletter/unsubscribe")
                        .param("email", "user@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Unsubscribed successfully"));
    }

    @Test
    void shouldRejectSubscribeWithInvalidEmail() throws Exception {
        NewsletterSubscribeRequest request = new NewsletterSubscribeRequest(
                "not-an-email", "Name", "PT");

        mockMvc.perform(post("/api/v1/public/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectSubscribeWithBlankEmail() throws Exception {
        NewsletterSubscribeRequest request = new NewsletterSubscribeRequest(
                "", "Name", "PT");

        mockMvc.perform(post("/api/v1/public/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- Admin endpoints ---

    @Test
    void shouldListSubscribers() throws Exception {
        when(newsletterService.findActiveSubscribers()).thenReturn(List.of(sampleSubscriber()));

        mockMvc.perform(get("/api/v1/newsletter/subscribers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].email").value("subscriber@email.com"));
    }

    @Test
    void shouldCountSubscribers() throws Exception {
        when(newsletterService.countActiveSubscribers()).thenReturn(42L);

        mockMvc.perform(get("/api/v1/newsletter/subscribers/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(42));
    }

    @Test
    void shouldDeleteSubscriber() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(newsletterService).delete(id);

        mockMvc.perform(delete("/api/v1/newsletter/subscribers/{id}", id))
                .andExpect(status().isNoContent());

        verify(newsletterService).delete(id);
    }
}
