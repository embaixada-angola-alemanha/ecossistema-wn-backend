package ao.gov.embaixada.wn.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.UUID;

public record ArticleCreateRequest(
        @NotBlank String slug,
        @NotBlank String tituloPt,
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
        UUID categoryId,
        UUID authorId,
        Set<UUID> tagIds,
        UUID featuredImageId,
        boolean featured
) {}
