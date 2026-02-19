package ao.gov.embaixada.wn.dto;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String slug,
        String nomePt,
        String nomeEn,
        String nomeDe
) {}
