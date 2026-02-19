package ao.gov.embaixada.wn.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String slug,
        String nomePt,
        String nomeEn,
        String nomeDe,
        String nomeCs,
        String descricaoPt,
        String descricaoEn,
        String descricaoDe,
        String cor,
        Integer sortOrder,
        boolean activo
) {}
