package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.wn.dto.CategoryCreateRequest;
import ao.gov.embaixada.wn.dto.CategoryResponse;
import ao.gov.embaixada.wn.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@PreAuthorize("hasAnyRole('WN-EDITOR','WN-ADMIN')")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponse> create(@Valid @RequestBody CategoryCreateRequest req) {
        return ApiResponse.success(service.create(req));
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> findById(@PathVariable UUID id) {
        return ApiResponse.success(service.findById(id));
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> findAll() {
        return ApiResponse.success(service.findAll());
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody CategoryCreateRequest req) {
        return ApiResponse.success(service.update(id, req));
    }

    @PatchMapping("/{id}/toggle-active")
    public ApiResponse<CategoryResponse> toggleActive(@PathVariable UUID id) {
        return ApiResponse.success(service.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
