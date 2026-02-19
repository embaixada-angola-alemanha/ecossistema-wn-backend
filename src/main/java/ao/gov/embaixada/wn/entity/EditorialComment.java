package ao.gov.embaixada.wn.entity;

import ao.gov.embaixada.commons.dto.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "editorial_comments")
public class EditorialComment extends BaseEntity {

    @Column(name = "article_id", nullable = false)
    private UUID articleId;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "author_name", nullable = false, length = 200)
    private String authorName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(length = 20)
    private String tipo = "COMMENT";

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(nullable = false)
    private boolean resolved = false;

    public UUID getArticleId() { return articleId; }
    public void setArticleId(UUID articleId) { this.articleId = articleId; }
    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
}
