package ao.gov.embaixada.wn.dto;

import ao.gov.embaixada.wn.enums.EstadoArtigo;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ArticleResponse(
        UUID id,
        String slug,
        String tituloPt,
        String tituloEn,
        String tituloDe,
        String tituloCs,
        String conteudoPt,
        String conteudoEn,
        String conteudoDe,
        String conteudoCs,
        String excertoPt,
        String excertoEn,
        String excertoDe,
        String metaTituloPt,
        String metaDescricaoPt,
        String metaKeywords,
        EstadoArtigo estado,
        CategoryResponse category,
        AuthorResponse author,
        Set<TagResponse> tags,
        UUID featuredImageId,
        boolean featured,
        Instant publishedAt,
        Instant scheduledAt,
        long viewCount,
        Instant createdAt,
        Instant updatedAt
) {}
