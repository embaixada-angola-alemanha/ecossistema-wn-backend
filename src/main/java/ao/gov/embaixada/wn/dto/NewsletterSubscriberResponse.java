package ao.gov.embaixada.wn.dto;

import java.time.Instant;
import java.util.UUID;

public record NewsletterSubscriberResponse(
        UUID id,
        String email,
        String nome,
        String idioma,
        boolean activo,
        boolean confirmed,
        Instant createdAt
) {}
