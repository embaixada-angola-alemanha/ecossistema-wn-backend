package ao.gov.embaixada.wn.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewsletterSubscribeRequest(
        @NotBlank @Email String email,
        String nome,
        String idioma
) {}
