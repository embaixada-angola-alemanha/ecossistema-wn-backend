package ao.gov.embaixada.wn.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "nome_pt", nullable = false, length = 200)
    private String nomePt;

    @Column(name = "nome_en", length = 200)
    private String nomeEn;

    @Column(name = "nome_de", length = 200)
    private String nomeDe;

    @Column(name = "nome_cs", length = 200)
    private String nomeCs;

    @Column(name = "descricao_pt", length = 500)
    private String descricaoPt;

    @Column(name = "descricao_en", length = 500)
    private String descricaoEn;

    @Column(name = "descricao_de", length = 500)
    private String descricaoDe;

    @Column(length = 50)
    private String cor;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private boolean activo = true;

    // Getters & Setters
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getNomePt() { return nomePt; }
    public void setNomePt(String nomePt) { this.nomePt = nomePt; }
    public String getNomeEn() { return nomeEn; }
    public void setNomeEn(String nomeEn) { this.nomeEn = nomeEn; }
    public String getNomeDe() { return nomeDe; }
    public void setNomeDe(String nomeDe) { this.nomeDe = nomeDe; }
    public String getNomeCs() { return nomeCs; }
    public void setNomeCs(String nomeCs) { this.nomeCs = nomeCs; }
    public String getDescricaoPt() { return descricaoPt; }
    public void setDescricaoPt(String descricaoPt) { this.descricaoPt = descricaoPt; }
    public String getDescricaoEn() { return descricaoEn; }
    public void setDescricaoEn(String descricaoEn) { this.descricaoEn = descricaoEn; }
    public String getDescricaoDe() { return descricaoDe; }
    public void setDescricaoDe(String descricaoDe) { this.descricaoDe = descricaoDe; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
