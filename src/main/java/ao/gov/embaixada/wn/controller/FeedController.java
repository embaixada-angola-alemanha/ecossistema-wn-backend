package ao.gov.embaixada.wn.controller;

import ao.gov.embaixada.wn.service.RssFeedService;
import com.rometools.rome.io.FeedException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final RssFeedService feedService;

    public FeedController(RssFeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping(value = "/rss", produces = "application/rss+xml;charset=UTF-8")
    public String rssFeed() throws FeedException {
        return feedService.generateRssFeed();
    }

    @GetMapping(value = "/atom", produces = "application/atom+xml;charset=UTF-8")
    public String atomFeed() throws FeedException {
        return feedService.generateAtomFeed();
    }
}
