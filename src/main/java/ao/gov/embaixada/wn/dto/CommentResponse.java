package ao.gov.embaixada.wn.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID articleId,
        UUID authorId,
        String authorName,
        String conteudo,
        String tipo,
        UUID parentId,
        boolean resolved,
        Instant createdAt,
        Instant updatedAt
) {}
