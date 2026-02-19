package ao.gov.embaixada.wn.dto;

import java.time.Instant;
import java.util.UUID;

public record ArticleVersionResponse(
        UUID id,
        UUID articleId,
        Long version,
        String tituloPt,
        String conteudoPt,
        String conteudoEn,
        String conteudoDe,
        String excertoPt,
        String changeSummary,
        String createdBy,
        Instant createdAt
) {}
