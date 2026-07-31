package dev.diegosaurus.cimb.callmonitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CallMonitoringSearchResponse(
        List<Item> items,
        long totalElements,
        int totalPages,
        int page,
        int size,
        String emptyStateMessage
) {
    public record Item(
            long number,
            String callId,
            LocalDateTime callTimestamp,
            String csAgentName,
            String customerName,
            BigDecimal sentimentScore
    ) { }
}
