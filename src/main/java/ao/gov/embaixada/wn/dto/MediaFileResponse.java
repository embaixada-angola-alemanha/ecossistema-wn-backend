package ao.gov.embaixada.wn.dto;

import java.time.Instant;
import java.util.UUID;

public record MediaFileResponse(
        UUID id,
        String fileName,
        String originalName,
        String mimeType,
        long size,
        String objectKey,
        String altPt,
        String altEn,
        String altDe,
        Integer width,
        Integer height,
        Instant createdAt
) {}
