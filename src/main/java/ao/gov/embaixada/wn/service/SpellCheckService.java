package ao.gov.embaixada.wn.service;

import ao.gov.embaixada.wn.dto.SpellCheckIssue;
import ao.gov.embaixada.wn.dto.SpellCheckResult;
import ao.gov.embaixada.wn.entity.Article;
import ao.gov.embaixada.wn.exception.ResourceNotFoundException;
import ao.gov.embaixada.wn.repository.ArticleRepository;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.RuleMatch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class SpellCheckService {

    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");
    private static final Pattern WHITESPACE_COLLAPSE = Pattern.compile("\\s+");

    private final ArticleRepository articleRepo;

    public SpellCheckService(ArticleRepository articleRepo) {
        this.articleRepo = articleRepo;
    }

    @Transactional(readOnly = true)
    public SpellCheckResult checkArticle(UUID articleId) {
        Article article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));

        Map<String, List<SpellCheckIssue>> issuesByField = new LinkedHashMap<>();
        int total = 0;

        // Check each populated language field
        total += checkField(article.getTituloPt(), "pt", "tituloPt", issuesByField);
        total += checkField(article.getConteudoPt(), "pt", "conteudoPt", issuesByField);
        total += checkField(article.getExcertoPt(), "pt", "excertoPt", issuesByField);

        total += checkField(article.getTituloEn(), "en", "tituloEn", issuesByField);
        total += checkField(article.getConteudoEn(), "en", "conteudoEn", issuesByField);

        total += checkField(article.getTituloDe(), "de", "tituloDe", issuesByField);
        total += checkField(article.getConteudoDe(), "de", "conteudoDe", issuesByField);

        return new SpellCheckResult(articleId, total, issuesByField);
    }

    public List<SpellCheckIssue> checkText(String text, String langCode) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String plainText = stripHtml(text);
        if (plainText.isBlank()) {
            return List.of();
        }

        Language language = resolveLanguage(langCode);
        if (language == null) {
            return List.of();
        }

        try {
            JLanguageTool tool = new JLanguageTool(language);
            List<RuleMatch> matches = tool.check(plainText);

            return matches.stream()
                    .map(m -> new SpellCheckIssue(
                            m.getMessage(),
                            extractContext(plainText, m.getFromPos(), m.getToPos()),
                            m.getFromPos(),
                            m.getToPos(),
                            m.getRule().getCategory().getName(),
                            m.getSuggestedReplacements().stream().limit(5).toList()
                    ))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Spell check failed", e);
        }
    }

    private int checkField(String content, String langCode, String fieldName,
                           Map<String, List<SpellCheckIssue>> issuesByField) {
        List<SpellCheckIssue> issues = checkText(content, langCode);
        if (!issues.isEmpty()) {
            issuesByField.put(fieldName, issues);
        }
        return issues.size();
    }

    private String stripHtml(String html) {
        String text = HTML_TAG.matcher(html).replaceAll(" ");
        text = text.replace("&nbsp;", " ")
                   .replace("&amp;", "&")
                   .replace("&lt;", "<")
                   .replace("&gt;", ">")
                   .replace("&quot;", "\"")
                   .replace("&#39;", "'");
        return WHITESPACE_COLLAPSE.matcher(text).replaceAll(" ").trim();
    }

    private Language resolveLanguage(String code) {
        try {
            return switch (code) {
                case "pt" -> Languages.getLanguageForShortCode("pt-PT");
                case "en" -> Languages.getLanguageForShortCode("en-GB");
                case "de" -> Languages.getLanguageForShortCode("de-DE");
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private String extractContext(String text, int from, int to) {
        int start = Math.max(0, from - 30);
        int end = Math.min(text.length(), to + 30);
        StringBuilder sb = new StringBuilder();
        if (start > 0) sb.append("...");
        sb.append(text, start, end);
        if (end < text.length()) sb.append("...");
        return sb.toString();
    }
}
