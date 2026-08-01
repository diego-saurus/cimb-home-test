package dev.diegosaurus.cimb.callmonitoring.dto;

import dev.diegosaurus.cimb.callmonitoring.domain.SentimentBucket;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CallMonitoringSearchRequest {
    @Size(max = 128) String search;
    LocalDate startDate;
    LocalDate endDate;
    SentimentBucket sentimentBucket;
    @Min(0) int page;
    @Min(1) int size;
    String sortBy;
    String direction;
}
