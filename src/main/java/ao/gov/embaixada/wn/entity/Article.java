package ao.gov.embaixada.wn.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "articles")
public class Article extends BaseEntity {

    @Column(nullable = false, unique = true, length = 300)
    private String slug;

    @Column(name = "titulo_pt", nullable = false, length = 300)
    private String tituloPt;

    @Column(name = "titulo_en", length = 300)
    private String tituloEn;

    @Column(name = "titulo_de", length = 300)
    private String tituloDe;

    @Column(name = "titulo_cs", length = 300)
    private String tituloCs;

    @Column(name = "conteudo_pt", columnDefinition = "TEXT")
    private String conteudoPt;

    @Column(name = "conteudo_en", columnDefinition = "TEXT")
    private String conteudoEn;

    @Column(name = "conteudo_de", columnDefinition = "TEXT")
    private String conteudoDe;

    @Column(name = "conteudo_cs", columnDefinition = "TEXT")
    private String conteudoCs;

    @Column(name = "excerto_pt", length = 500)
    private String excertoPt;

    @Column(name = "excerto_en", length = 500)
    private String excertoEn;

    @Column(name = "excerto_de", length = 500)
    private String excertoDe;

    @Column(name = "meta_titulo_pt", length = 160)
    private String metaTituloPt;

    @Column(name = "meta_descricao_pt", length = 320)
    private String metaDescricaoPt;

    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoArtigo estado = EstadoArtigo.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "featured_image_id")
    private UUID featuredImageId;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "view_count")
    private long viewCount = 0;

    // Getters & Setters
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getTituloPt() { return tituloPt; }
    public void setTituloPt(String tituloPt) { this.tituloPt = tituloPt; }
    public String getTituloEn() { return tituloEn; }
    public void setTituloEn(String tituloEn) { this.tituloEn = tituloEn; }
    public String getTituloDe() { return tituloDe; }
    public void setTituloDe(String tituloDe) { this.tituloDe = tituloDe; }
    public String getTituloCs() { return tituloCs; }
    public void setTituloCs(String tituloCs) { this.tituloCs = tituloCs; }
    public String getConteudoPt() { return conteudoPt; }
    public void setConteudoPt(String conteudoPt) { this.conteudoPt = conteudoPt; }
    public String getConteudoEn() { return conteudoEn; }
    public void setConteudoEn(String conteudoEn) { this.conteudoEn = conteudoEn; }
    public String getConteudoDe() { return conteudoDe; }
    public void setConteudoDe(String conteudoDe) { this.conteudoDe = conteudoDe; }
    public String getConteudoCs() { return conteudoCs; }
    public void setConteudoCs(String conteudoCs) { this.conteudoCs = conteudoCs; }
    public String getExcertoPt() { return excertoPt; }
    public void setExcertoPt(String excertoPt) { this.excertoPt = excertoPt; }
    public String getExcertoEn() { return excertoEn; }
    public void setExcertoEn(String excertoEn) { this.excertoEn = excertoEn; }
    public String getExcertoDe() { return excertoDe; }
    public void setExcertoDe(String excertoDe) { this.excertoDe = excertoDe; }
    public String getMetaTituloPt() { return metaTituloPt; }
    public void setMetaTituloPt(String metaTituloPt) { this.metaTituloPt = metaTituloPt; }
    public String getMetaDescricaoPt() { return metaDescricaoPt; }
    public void setMetaDescricaoPt(String metaDescricaoPt) { this.metaDescricaoPt = metaDescricaoPt; }
    public String getMetaKeywords() { return metaKeywords; }
    public void setMetaKeywords(String metaKeywords) { this.metaKeywords = metaKeywords; }
    public EstadoArtigo getEstado() { return estado; }
    public void setEstado(EstadoArtigo estado) { this.estado = estado; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }
    public UUID getFeaturedImageId() { return featuredImageId; }
    public void setFeaturedImageId(UUID featuredImageId) { this.featuredImageId = featuredImageId; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
    public Instant getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Instant scheduledAt) { this.scheduledAt = scheduledAt; }
    public long getViewCount() { return viewCount; }
    public void setViewCount(long viewCount) { this.viewCount = viewCount; }
}
