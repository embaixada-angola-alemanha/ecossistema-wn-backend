package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.ShareResponse;
import ao.gov.embaixada.wn.entity.Article;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ShareService {

    private final ArticleRepository articleRepo;

    @Value("${wn.site-url}")
    private String siteUrl;

    public ShareService(ArticleRepository articleRepo) {
        this.articleRepo = articleRepo;
    }

    public ShareResponse getShareLinks(UUID articleId) {
        Article a = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        String articleUrl = siteUrl + "/artigos/" + a.getSlug();
        String title = a.getTituloPt();
        String encodedUrl = URLEncoder.encode(articleUrl, StandardCharsets.UTF_8);
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

        Map<String, String> links = new LinkedHashMap<>();
        links.put("facebook", "https://www.facebook.com/sharer/sharer.php?u=" + encodedUrl);
        links.put("twitter", "https://twitter.com/intent/tweet?url=" + encodedUrl + "&text=" + encodedTitle);
        links.put("linkedin", "https://www.linkedin.com/sharing/share-offsite/?url=" + encodedUrl);
        links.put("whatsapp", "https://wa.me/?text=" + encodedTitle + "%20" + encodedUrl);
        links.put("telegram", "https://t.me/share/url?url=" + encodedUrl + "&text=" + encodedTitle);
        links.put("email", "mailto:?subject=" + encodedTitle + "&body=" + encodedUrl);

        return new ShareResponse(articleUrl, title, links);
    }

    public ShareResponse getShareLinksBySlug(String slug) {
        Article a = articleRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found: " + slug));
        return getShareLinks(a.getId());
    }
}
