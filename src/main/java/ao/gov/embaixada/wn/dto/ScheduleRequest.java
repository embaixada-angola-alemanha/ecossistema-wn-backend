package ao.gov.embaixada.wn.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ScheduleRequest(
        @NotNull Instant scheduledAt
) {}
