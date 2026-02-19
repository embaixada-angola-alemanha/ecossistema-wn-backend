package ao.gov.embaixada.wn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CommentCreateRequest(
        @NotNull UUID authorId,
        @NotBlank String conteudo,
        String tipo,
        UUID parentId
) {}
