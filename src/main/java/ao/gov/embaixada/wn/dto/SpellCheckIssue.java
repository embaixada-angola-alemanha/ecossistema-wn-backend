package ao.gov.embaixada.wn.dto;

import java.util.List;

public record SpellCheckIssue(
    String message,
    String context,
    int fromPos,
    int toPos,
    String category,
    List<String> suggestions
) {}
