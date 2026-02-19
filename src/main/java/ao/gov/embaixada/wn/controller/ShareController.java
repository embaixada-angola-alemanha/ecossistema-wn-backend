package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.wn.dto.ShareResponse;
import ao.gov.embaixada.wn.service.ShareService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/articles")
public class ShareController {

    private final ShareService service;

    public ShareController(ShareService service) {
        this.service = service;
    }

    @GetMapping("/{slug}/share")
    public ApiResponse<ShareResponse> getShareLinks(@PathVariable String slug) {
        return ApiResponse.success(service.getShareLinksBySlug(slug));
    }
}
