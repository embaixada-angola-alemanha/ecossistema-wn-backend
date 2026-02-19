package ao.gov.embaixada.wn.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "authors")
public class Author extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(unique = true, length = 100)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String bioPt;

    @Column(columnDefinition = "TEXT")
    private String bioEn;

    @Column(columnDefinition = "TEXT")
    private String bioDe;

    @Column(length = 200)
    private String email;

    @Column(name = "avatar_id")
    private UUID avatarId;

    @Column(name = "keycloak_id", length = 200)
    private String keycloakId;

    @Column(length = 50)
    private String role;

    @Column(nullable = false)
    private boolean activo = true;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getBioPt() { return bioPt; }
    public void setBioPt(String bioPt) { this.bioPt = bioPt; }
    public String getBioEn() { return bioEn; }
    public void setBioEn(String bioEn) { this.bioEn = bioEn; }
    public String getBioDe() { return bioDe; }
    public void setBioDe(String bioDe) { this.bioDe = bioDe; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public UUID getAvatarId() { return avatarId; }
    public void setAvatarId(UUID avatarId) { this.avatarId = avatarId; }
    public String getKeycloakId() { return keycloakId; }
    public void setKeycloakId(String keycloakId) { this.keycloakId = keycloakId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
