package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.entity.Article;
import ao.gov.embaixada.wn.enums.EstadoArtigo;
import ao.gov.embaixada.wn.repository.ArticleRepository;
import com.rometools.rome.feed.atom.*;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.feed.synd.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RssFeedService {

    private final ArticleRepository articleRepo;

    @Value("${wn.site-url}")
    private String siteUrl;

    @Value("${wn.site-name}")
    private String siteName;

    @Value("${wn.feed.title}")
    private String feedTitle;

    @Value("${wn.feed.description}")
    private String feedDescription;

    @Value("${wn.feed.language}")
    private String feedLanguage;

    @Value("${wn.feed.max-items}")
    private int maxItems;

    public RssFeedService(ArticleRepository articleRepo) {
        this.articleRepo = articleRepo;
    }

    public String generateRssFeed() throws FeedException {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle(feedTitle);
        feed.setLink(siteUrl);
        feed.setDescription(feedDescription);
        feed.setLanguage(feedLanguage);

        List<Article> articles = articleRepo.findByEstadoOrderByPublishedAtDesc(
                EstadoArtigo.PUBLISHED, PageRequest.of(0, maxItems)).getContent();

        List<SyndEntry> entries = new ArrayList<>();
        for (Article a : articles) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(a.getTituloPt());
            entry.setLink(siteUrl + "/artigos/" + a.getSlug());
            entry.setUri(a.getId().toString());
            if (a.getPublishedAt() != null) {
                entry.setPublishedDate(Date.from(a.getPublishedAt()));
            }
            if (a.getAuthor() != null) {
                entry.setAuthor(a.getAuthor().getNome());
            }

            SyndContent desc = new SyndContentImpl();
            desc.setType("text/plain");
            desc.setValue(a.getExcertoPt() != null ? a.getExcertoPt() : "");
            entry.setDescription(desc);

            if (a.getCategory() != null) {
                SyndCategory cat = new SyndCategoryImpl();
                cat.setName(a.getCategory().getNomePt());
                entry.setCategories(List.of(cat));
            }

            entries.add(entry);
        }

        feed.setEntries(entries);
        return new SyndFeedOutput().outputString(feed);
    }

    public String generateAtomFeed() throws FeedException {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("atom_1.0");
        feed.setTitle(feedTitle);
        feed.setLink(siteUrl);
        feed.setDescription(feedDescription);

        List<Article> articles = articleRepo.findByEstadoOrderByPublishedAtDesc(
                EstadoArtigo.PUBLISHED, PageRequest.of(0, maxItems)).getContent();

        List<SyndEntry> entries = new ArrayList<>();
        for (Article a : articles) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(a.getTituloPt());
            entry.setLink(siteUrl + "/artigos/" + a.getSlug());
            entry.setUri(siteUrl + "/artigos/" + a.getSlug());
            if (a.getPublishedAt() != null) {
                entry.setPublishedDate(Date.from(a.getPublishedAt()));
                entry.setUpdatedDate(Date.from(a.getPublishedAt()));
            }
            if (a.getAuthor() != null) {
                entry.setAuthor(a.getAuthor().getNome());
            }

            SyndContent content = new SyndContentImpl();
            content.setType("text/html");
            content.setValue(a.getExcertoPt() != null ? a.getExcertoPt() : "");
            entry.setDescription(content);

            entries.add(entry);
        }

        feed.setEntries(entries);
        return new SyndFeedOutput().outputString(feed);
    }
}
