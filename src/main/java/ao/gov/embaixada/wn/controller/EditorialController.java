package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.wn.dto.*;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import ao.gov.embaixada.wn.service.ArticleService;
import ao.gov.embaixada.wn.service.ArticleVersionService;
import ao.gov.embaixada.wn.service.EditorialCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/editorial")
@PreAuthorize("hasAnyRole('wn-editor','wn-journalist','wn-reviewer','wn-admin')")
public class EditorialController {

    private final ArticleService articleService;
    private final ArticleVersionService versionService;
    private final EditorialCommentService commentService;

    public EditorialController(ArticleService articleService,
                                ArticleVersionService versionService,
                                EditorialCommentService commentService) {
        this.articleService = articleService;
        this.versionService = versionService;
        this.commentService = commentService;
    }

    // --- Workflow state transitions ---

    @PatchMapping("/articles/{id}/submit")
    @PreAuthorize("hasAnyRole('wn-journalist','wn-editor','wn-admin')")
    public ApiResponse<ArticleResponse> submit(@PathVariable UUID id) {
        versionService.createVersion(id, "Submitted for review", "workflow");
        return ApiResponse.success(articleService.updateEstado(id, EstadoArtigo.SUBMITTED));
    }

    @PatchMapping("/articles/{id}/review")
    @PreAuthorize("hasAnyRole('wn-editor','wn-reviewer','wn-admin')")
    public ApiResponse<ArticleResponse> startReview(@PathVariable UUID id) {
        return ApiResponse.success(articleService.updateEstado(id, EstadoArtigo.IN_REVIEW));
    }

    @PatchMapping("/articles/{id}/publish")
    @PreAuthorize("hasAnyRole('wn-editor','wn-admin')")
    public ApiResponse<ArticleResponse> publish(@PathVariable UUID id) {
        versionService.createVersion(id, "Published", "workflow");
        return ApiResponse.success(articleService.updateEstado(id, EstadoArtigo.PUBLISHED));
    }

    @PatchMapping("/articles/{id}/reject")
    @PreAuthorize("hasAnyRole('wn-editor','wn-reviewer','wn-admin')")
    public ApiResponse<ArticleResponse> reject(@PathVariable UUID id) {
        return ApiResponse.success(articleService.updateEstado(id, EstadoArtigo.DRAFT));
    }

    @PatchMapping("/articles/{id}/archive")
    @PreAuthorize("hasAnyRole('wn-editor','wn-admin')")
    public ApiResponse<ArticleResponse> archive(@PathVariable UUID id) {
        return ApiResponse.success(articleService.updateEstado(id, EstadoArtigo.ARCHIVED));
    }

    // --- Scheduling ---

    @PatchMapping("/articles/{id}/schedule")
    @PreAuthorize("hasAnyRole('wn-editor','wn-admin')")
    public ApiResponse<ArticleResponse> schedule(@PathVariable UUID id,
                                                  @Valid @RequestBody ScheduleRequest req) {
        return ApiResponse.success(articleService.schedulePublication(id, req.scheduledAt()));
    }

    @DeleteMapping("/articles/{id}/schedule")
    @PreAuthorize("hasAnyRole('wn-editor','wn-admin')")
    public ApiResponse<ArticleResponse> cancelSchedule(@PathVariable UUID id) {
        return ApiResponse.success(articleService.cancelSchedule(id));
    }

    // --- Versioning ---

    @GetMapping("/articles/{id}/versions")
    public ApiResponse<List<ArticleVersionResponse>> listVersions(@PathVariable UUID id) {
        return ApiResponse.success(versionService.findByArticle(id));
    }

    @GetMapping("/articles/{id}/versions/{version}")
    public ApiResponse<ArticleVersionResponse> getVersion(@PathVariable UUID id,
                                                           @PathVariable int version) {
        return ApiResponse.success(versionService.findByArticleAndVersion(id, version));
    }

    @PostMapping("/articles/{id}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ArticleVersionResponse> createVersion(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "") String summary,
            @RequestParam(defaultValue = "manual") String createdBy) {
        return ApiResponse.success(versionService.createVersion(id, summary, createdBy));
    }

    @PostMapping("/articles/{id}/versions/{version}/restore")
    public ApiResponse<Map<String, String>> restoreVersion(@PathVariable UUID id,
                                                            @PathVariable int version) {
        versionService.restoreVersion(id, version);
        return ApiResponse.success(Map.of("message", "Restored to version " + version));
    }

    // --- Comments ---

    @GetMapping("/articles/{id}/comments")
    public ApiResponse<List<CommentResponse>> listComments(@PathVariable UUID id) {
        return ApiResponse.success(commentService.findByArticle(id));
    }

    @GetMapping("/articles/{id}/comments/unresolved")
    public ApiResponse<List<CommentResponse>> listUnresolved(@PathVariable UUID id) {
        return ApiResponse.success(commentService.findUnresolved(id));
    }

    @PostMapping("/articles/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> addComment(@PathVariable UUID id,
                                                    @Valid @RequestBody CommentCreateRequest req) {
        return ApiResponse.success(commentService.addComment(id, req));
    }

    @PatchMapping("/comments/{commentId}/resolve")
    public ApiResponse<CommentResponse> resolveComment(@PathVariable UUID commentId) {
        return ApiResponse.success(commentService.resolveComment(commentId));
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
    }
}
