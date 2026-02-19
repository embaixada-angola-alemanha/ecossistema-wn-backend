package ao.gov.embaixada.wn.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank String slug,
        @NotBlank String nomePt,
        String nomeEn,
        String nomeDe,
        String nomeCs,
        String descricaoPt,
        String descricaoEn,
        String descricaoDe,
        String cor,
        Integer sortOrder
) {}
