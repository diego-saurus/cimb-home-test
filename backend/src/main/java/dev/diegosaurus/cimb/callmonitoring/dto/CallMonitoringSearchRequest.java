package dev.diegosaurus.cimb.callmonitoring.dto;

import dev.diegosaurus.cimb.callmonitoring.domain.SentimentBucket;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CallMonitoringSearchRequest(
        @Size(max = 128) String search,
        LocalDate startDate,
        LocalDate endDate,
        SentimentBucket sentimentBucket,
        @Min(0) int page,
        @Min(1) int size,
        String sortBy,
        String direction
) {
    public static Builder builderWithDefaults() {
        return new Builder();
    }

    public static final class Builder {
        private String search;
        private LocalDate startDate;
        private LocalDate endDate;
        private SentimentBucket sentimentBucket;
        private int page;
        private int size;
        private String sortBy;
        private String direction;

        public Builder search(String v) { this.search = v; return this; }
        public Builder startDate(LocalDate v) { this.startDate = v; return this; }
        public Builder endDate(LocalDate v) { this.endDate = v; return this; }
        public Builder sentimentBucket(SentimentBucket v) { this.sentimentBucket = v; return this; }
        public Builder page(int v) { this.page = v; return this; }
        public Builder size(int v) { this.size = v; return this; }
        public Builder sortBy(String v) { this.sortBy = v; return this; }
        public Builder direction(String v) { this.direction = v; return this; }

        public CallMonitoringSearchRequest build() {
            return new CallMonitoringSearchRequest(
                    search, startDate, endDate, sentimentBucket,
                    page, size, sortBy, direction);
        }
    }
}
