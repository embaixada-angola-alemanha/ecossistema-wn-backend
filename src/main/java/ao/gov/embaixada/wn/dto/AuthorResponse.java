package ao.gov.embaixada.wn.dto;

import java.util.UUID;

public record AuthorResponse(
        UUID id,
        String nome,
        String slug,
        String bioPt,
        String bioEn,
        String bioDe,
        String email,
        UUID avatarId,
        String role,
        boolean activo
) {}
