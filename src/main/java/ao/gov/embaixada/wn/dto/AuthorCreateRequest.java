package ao.gov.embaixada.wn.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AuthorCreateRequest(
        @NotBlank String nome,
        String slug,
        String bioPt,
        String bioEn,
        String bioDe,
        String email,
        UUID avatarId,
        String keycloakId,
        String role
) {}
