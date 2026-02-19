package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.wn.dto.TagCreateRequest;
import ao.gov.embaixada.wn.dto.TagResponse;
import ao.gov.embaixada.wn.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tags")
@PreAuthorize("hasAnyRole('wn-editor','wn-admin')")
public class TagController {

    private final TagService service;

    public TagController(TagService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TagResponse> create(@Valid @RequestBody TagCreateRequest req) {
        return ApiResponse.success(service.create(req));
    }

    @GetMapping("/{id}")
    public ApiResponse<TagResponse> findById(@PathVariable UUID id) {
        return ApiResponse.success(service.findById(id));
    }

    @GetMapping
    public ApiResponse<List<TagResponse>> findAll() {
        return ApiResponse.success(service.findAll());
    }

    @PutMapping("/{id}")
    public ApiResponse<TagResponse> update(@PathVariable UUID id,
                                            @Valid @RequestBody TagCreateRequest req) {
        return ApiResponse.success(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
