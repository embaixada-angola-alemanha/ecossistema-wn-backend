package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.wn.dto.MediaFileResponse;
import ao.gov.embaixada.wn.service.MediaService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/media")
public class PublicMediaController {

    private final MediaService service;

    public PublicMediaController(MediaService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> serve(@PathVariable UUID id) {
        MediaFileResponse meta = service.findById(id);
        InputStream stream = service.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(meta.mimeType()))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + meta.fileName() + "\"")
                .body(new InputStreamResource(stream));
    }
}
