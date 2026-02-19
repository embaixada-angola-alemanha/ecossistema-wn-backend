package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.wn.dto.AuthorCreateRequest;
import ao.gov.embaixada.wn.dto.AuthorResponse;
import ao.gov.embaixada.wn.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authors")
@PreAuthorize("hasAnyRole('wn-editor','wn-admin')")
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthorResponse> create(@Valid @RequestBody AuthorCreateRequest req) {
        return ApiResponse.success(service.create(req));
    }

    @GetMapping("/{id}")
    public ApiResponse<AuthorResponse> findById(@PathVariable UUID id) {
        return ApiResponse.success(service.findById(id));
    }

    @GetMapping
    public ApiResponse<List<AuthorResponse>> findAll() {
        return ApiResponse.success(service.findAll());
    }

    @GetMapping("/active")
    public ApiResponse<List<AuthorResponse>> findActive() {
        return ApiResponse.success(service.findActive());
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ApiResponse<AuthorResponse> findByKeycloakId(@PathVariable String keycloakId) {
        return ApiResponse.success(service.findByKeycloakId(keycloakId));
    }

    @PutMapping("/{id}")
    public ApiResponse<AuthorResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody AuthorCreateRequest req) {
        return ApiResponse.success(service.update(id, req));
    }

    @PatchMapping("/{id}/toggle-active")
    public ApiResponse<AuthorResponse> toggleActive(@PathVariable UUID id) {
        return ApiResponse.success(service.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
