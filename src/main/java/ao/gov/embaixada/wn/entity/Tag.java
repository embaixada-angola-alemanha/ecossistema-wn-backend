package ao.gov.embaixada.wn.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "nome_pt", nullable = false, length = 100)
    private String nomePt;

    @Column(name = "nome_en", length = 100)
    private String nomeEn;

    @Column(name = "nome_de", length = 100)
    private String nomeDe;

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getNomePt() { return nomePt; }
    public void setNomePt(String nomePt) { this.nomePt = nomePt; }
    public String getNomeEn() { return nomeEn; }
    public void setNomeEn(String nomeEn) { this.nomeEn = nomeEn; }
    public String getNomeDe() { return nomeDe; }
    public void setNomeDe(String nomeDe) { this.nomeDe = nomeDe; }
}
