package ao.gov.embaixada.wn.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SpellCheckResult(
    UUID articleId,
    int totalIssues,
    Map<String, List<SpellCheckIssue>> issuesByField
) {}
