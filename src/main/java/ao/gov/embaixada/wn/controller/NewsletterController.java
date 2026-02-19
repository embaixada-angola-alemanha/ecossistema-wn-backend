package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.wn.dto.NewsletterSubscribeRequest;
import ao.gov.embaixada.wn.dto.NewsletterSubscriberResponse;
import ao.gov.embaixada.wn.service.NewsletterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class NewsletterController {

    private final NewsletterService service;

    public NewsletterController(NewsletterService service) {
        this.service = service;
    }

    // --- Public endpoints ---

    @PostMapping("/api/v1/public/newsletter/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<NewsletterSubscriberResponse> subscribe(
            @Valid @RequestBody NewsletterSubscribeRequest req) {
        return ApiResponse.success(service.subscribe(req));
    }

    @GetMapping("/api/v1/public/newsletter/confirm")
    public ApiResponse<Map<String, String>> confirm(@RequestParam String token) {
        service.confirm(token);
        return ApiResponse.success(Map.of("message", "Subscription confirmed"));
    }

    @PostMapping("/api/v1/public/newsletter/unsubscribe")
    public ApiResponse<Map<String, String>> unsubscribe(@RequestParam String email) {
        service.unsubscribe(email);
        return ApiResponse.success(Map.of("message", "Unsubscribed successfully"));
    }

    // --- Admin endpoints ---

    @GetMapping("/api/v1/newsletter/subscribers")
    @PreAuthorize("hasAnyRole('WN-EDITOR','WN-ADMIN')")
    public ApiResponse<List<NewsletterSubscriberResponse>> listSubscribers() {
        return ApiResponse.success(service.findActiveSubscribers());
    }

    @GetMapping("/api/v1/newsletter/subscribers/count")
    @PreAuthorize("hasAnyRole('WN-EDITOR','WN-ADMIN')")
    public ApiResponse<Map<String, Long>> countSubscribers() {
        return ApiResponse.success(Map.of("count", service.countActiveSubscribers()));
    }

    @DeleteMapping("/api/v1/newsletter/subscribers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('WN-ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
