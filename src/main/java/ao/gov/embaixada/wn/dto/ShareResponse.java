package ao.gov.embaixada.wn.dto;

import java.util.Map;

public record ShareResponse(
        String articleUrl,
        String title,
        Map<String, String> shareLinks
) {}
