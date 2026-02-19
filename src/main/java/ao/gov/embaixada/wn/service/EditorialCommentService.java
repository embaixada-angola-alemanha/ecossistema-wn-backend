package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.CommentCreateRequest;
import ao.gov.embaixada.wn.dto.CommentResponse;
import ao.gov.embaixada.wn.entity.Author;
import ao.gov.embaixada.wn.entity.EditorialComment;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.AuthorRepository;
import ao.gov.embaixada.wn.repository.EditorialCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EditorialCommentService {

    private final EditorialCommentRepository commentRepo;
    private final AuthorRepository authorRepo;

    public EditorialCommentService(EditorialCommentRepository commentRepo,
                                    AuthorRepository authorRepo) {
        this.commentRepo = commentRepo;
        this.authorRepo = authorRepo;
    }

    public CommentResponse addComment(UUID articleId, CommentCreateRequest req) {
        Author author = authorRepo.findById(req.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author", req.authorId()));

        EditorialComment c = new EditorialComment();
        c.setArticleId(articleId);
        c.setAuthorId(req.authorId());
        c.setAuthorName(author.getNome());
        c.setConteudo(req.conteudo());
        c.setTipo(req.tipo() != null ? req.tipo() : "COMMENT");
        c.setParentId(req.parentId());
        return toResponse(commentRepo.save(c));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findByArticle(UUID articleId) {
        return commentRepo.findByArticleIdOrderByCreatedAtAsc(articleId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findUnresolved(UUID articleId) {
        return commentRepo.findByArticleIdAndResolvedFalseOrderByCreatedAtAsc(articleId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public int countUnresolved(UUID articleId) {
        return commentRepo.countByArticleIdAndResolvedFalse(articleId);
    }

    public CommentResponse resolveComment(UUID commentId) {
        EditorialComment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
        c.setResolved(true);
        return toResponse(commentRepo.save(c));
    }

    public void deleteComment(UUID commentId) {
        if (!commentRepo.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment", commentId);
        }
        commentRepo.deleteById(commentId);
    }

    private CommentResponse toResponse(EditorialComment c) {
        return new CommentResponse(
                c.getId(), c.getArticleId(), c.getAuthorId(), c.getAuthorName(),
                c.getConteudo(), c.getTipo(), c.getParentId(),
                c.isResolved(), c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}
