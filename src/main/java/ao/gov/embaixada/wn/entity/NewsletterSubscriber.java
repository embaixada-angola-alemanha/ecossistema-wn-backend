package ao.gov.embaixada.wn.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "newsletter_subscribers")
public class NewsletterSubscriber extends BaseEntity {

    @Column(nullable = false, unique = true, length = 300)
    private String email;

    @Column(length = 200)
    private String nome;

    @Column(length = 10)
    private String idioma = "pt";

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "confirmation_token", length = 100)
    private String confirmationToken;

    @Column(nullable = false)
    private boolean confirmed = false;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getConfirmationToken() { return confirmationToken; }
    public void setConfirmationToken(String confirmationToken) { this.confirmationToken = confirmationToken; }
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
}
