package ao.gov.embaixada.wn.dto;

import jakarta.validation.constraints.NotBlank;

public record TagCreateRequest(
        @NotBlank String slug,
        @NotBlank String nomePt,
        String nomeEn,
        String nomeDe
) {}
