package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.commons.dto.ApiResponse;
import ao.gov.embaixada.commons.dto.PagedResponse;
import ao.gov.embaixada.wn.dto.MediaFileResponse;
import ao.gov.embaixada.wn.service.MediaService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@PreAuthorize("hasAnyRole('WN-EDITOR','WN-JOURNALIST','WN-ADMIN')")
public class MediaController {

    private final MediaService service;

    public MediaController(MediaService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MediaFileResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String altPt,
            @RequestParam(required = false) String altEn,
            @RequestParam(required = false) String altDe) throws IOException {
        return ApiResponse.success(service.upload(file, altPt, altEn, altDe));
    }

    @PostMapping(value = "/resize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MediaFileResponse> uploadResized(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "1200") int maxWidth,
            @RequestParam(defaultValue = "800") int maxHeight,
            @RequestParam(required = false) String altPt,
            @RequestParam(required = false) String altEn,
            @RequestParam(required = false) String altDe) throws IOException {
        return ApiResponse.success(service.uploadResized(file, maxWidth, maxHeight, altPt, altEn, altDe));
    }

    @GetMapping("/{id}")
    public ApiResponse<MediaFileResponse> findById(@PathVariable UUID id) {
        return ApiResponse.success(service.findById(id));
    }

    @GetMapping
    public ApiResponse<PagedResponse<MediaFileResponse>> findAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PagedResponse.of(service.findAll(pageable)));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID id) {
        MediaFileResponse meta = service.findById(id);
        InputStream stream = service.download(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.originalName() + "\"")
                .contentType(MediaType.parseMediaType(meta.mimeType()))
                .body(new InputStreamResource(stream));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('WN-EDITOR','WN-ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
