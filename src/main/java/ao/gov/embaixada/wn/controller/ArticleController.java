package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.commons.dto.PagedResponse;
import ao.gov.embaixada.wn.dto.ArticleCreateRequest;
import ao.gov.embaixada.wn.dto.ArticleResponse;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import ao.gov.embaixada.wn.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/articles")
@PreAuthorize("hasAnyRole('WN-EDITOR','WN-JOURNALIST','WN-ADMIN')")
public class ArticleController {

    private final ArticleService service;

    public ArticleController(ArticleService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ArticleResponse> create(@Valid @RequestBody ArticleCreateRequest req) {
        return ApiResponse.success(service.create(req));
    }

    @GetMapping("/{id}")
    public ApiResponse<ArticleResponse> findById(@PathVariable UUID id) {
        return ApiResponse.success(service.findById(id));
    }

    @GetMapping
    public ApiResponse<PagedResponse<ArticleResponse>> findAll(
            @RequestParam(required = false) EstadoArtigo estado,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ArticleResponse> page = (estado != null)
                ? service.findByEstado(estado, pageable)
                : service.findAll(pageable);
        return ApiResponse.success(PagedResponse.of(page));
    }

    @GetMapping("/editorial")
    public ApiResponse<PagedResponse<ArticleResponse>> findEditorial(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PagedResponse.of(service.findEditorial(pageable)));
    }

    @PutMapping("/{id}")
    public ApiResponse<ArticleResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody ArticleCreateRequest req) {
        return ApiResponse.success(service.update(id, req));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('WN-EDITOR','WN-ADMIN')")
    public ApiResponse<ArticleResponse> updateEstado(@PathVariable UUID id,
                                                      @RequestParam EstadoArtigo estado) {
        return ApiResponse.success(service.updateEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('WN-EDITOR','WN-ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
