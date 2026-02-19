package ao.gov.embaixada.wn.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "article_versions")
public class ArticleVersion extends BaseEntity {

    @Column(name = "article_id", nullable = false)
    private UUID articleId;

    @Column(nullable = false)
    private Long version;

    @Column(name = "titulo_pt", length = 300)
    private String tituloPt;

    @Column(name = "conteudo_pt", columnDefinition = "TEXT")
    private String conteudoPt;

    @Column(name = "conteudo_en", columnDefinition = "TEXT")
    private String conteudoEn;

    @Column(name = "conteudo_de", columnDefinition = "TEXT")
    private String conteudoDe;

    @Column(name = "excerto_pt", length = 500)
    private String excertoPt;

    @Column(name = "snapshot", columnDefinition = "TEXT")
    private String snapshot;

    @Column(name = "change_summary", length = 500)
    private String changeSummary;

    @Column(name = "created_by", length = 200)
    private String createdBy;

    public UUID getArticleId() { return articleId; }
    public void setArticleId(UUID articleId) { this.articleId = articleId; }
    @Override
    public Long getVersion() { return version; }
    @Override
    public void setVersion(Long version) { this.version = version; }
    public String getTituloPt() { return tituloPt; }
    public void setTituloPt(String tituloPt) { this.tituloPt = tituloPt; }
    public String getConteudoPt() { return conteudoPt; }
    public void setConteudoPt(String conteudoPt) { this.conteudoPt = conteudoPt; }
    public String getConteudoEn() { return conteudoEn; }
    public void setConteudoEn(String conteudoEn) { this.conteudoEn = conteudoEn; }
    public String getConteudoDe() { return conteudoDe; }
    public void setConteudoDe(String conteudoDe) { this.conteudoDe = conteudoDe; }
    public String getExcertoPt() { return excertoPt; }
    public void setExcertoPt(String excertoPt) { this.excertoPt = excertoPt; }
    public String getSnapshot() { return snapshot; }
    public void setSnapshot(String snapshot) { this.snapshot = snapshot; }
    public String getChangeSummary() { return changeSummary; }
    public void setChangeSummary(String changeSummary) { this.changeSummary = changeSummary; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
