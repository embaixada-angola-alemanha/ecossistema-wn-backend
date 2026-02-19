package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.commons.dto.PagedResponse;
import ao.gov.embaixada.wn.dto.ArticleResponse;
import ao.gov.embaixada.wn.dto.CategoryResponse;
import ao.gov.embaixada.wn.dto.TagResponse;
import ao.gov.embaixada.wn.service.ArticleService;
import ao.gov.embaixada.wn.service.CategoryService;
import ao.gov.embaixada.wn.service.TagService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/articles")
public class PublicArticleController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public PublicArticleController(ArticleService articleService,
                                   CategoryService categoryService,
                                   TagService tagService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @GetMapping
    public ApiResponse<PagedResponse<ArticleResponse>> listPublished(
            @PageableDefault(size = 12) Pageable pageable) {
        return ApiResponse.success(PagedResponse.of(articleService.findPublished(pageable)));
    }

    @GetMapping("/{slug}")
    public ApiResponse<ArticleResponse> findBySlug(@PathVariable String slug) {
        ArticleResponse article = articleService.findBySlug(slug);
        articleService.incrementViewCount(article.id());
        return ApiResponse.success(article);
    }

    @GetMapping("/featured")
    public ApiResponse<List<ArticleResponse>> findFeatured() {
        return ApiResponse.success(articleService.findFeatured());
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<PagedResponse<ArticleResponse>> findByCategory(
            @PathVariable UUID categoryId,
            @PageableDefault(size = 12) Pageable pageable) {
        return ApiResponse.success(PagedResponse.of(articleService.findByCategory(categoryId, pageable)));
    }

    @GetMapping("/tag/{tagId}")
    public ApiResponse<PagedResponse<ArticleResponse>> findByTag(
            @PathVariable UUID tagId,
            @PageableDefault(size = 12) Pageable pageable) {
        return ApiResponse.success(PagedResponse.of(articleService.findByTag(tagId, pageable)));
    }

    @GetMapping("/author/{authorId}")
    public ApiResponse<PagedResponse<ArticleResponse>> findByAuthor(
            @PathVariable UUID authorId,
            @PageableDefault(size = 12) Pageable pageable) {
        return ApiResponse.success(PagedResponse.of(articleService.findByAuthor(authorId, pageable)));
    }

    @GetMapping("/search")
    public ApiResponse<PagedResponse<ArticleResponse>> search(
            @RequestParam String q,
            @PageableDefault(size = 12) Pageable pageable) {
        return ApiResponse.success(PagedResponse.of(articleService.search(q, pageable)));
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> listCategories() {
        return ApiResponse.success(categoryService.findActive());
    }

    @GetMapping("/tags")
    public ApiResponse<List<TagResponse>> listTags() {
        return ApiResponse.success(tagService.findAll());
    }
}
