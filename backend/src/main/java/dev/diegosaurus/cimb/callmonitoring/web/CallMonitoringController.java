package dev.diegosaurus.cimb.callmonitoring.web;

import dev.diegosaurus.cimb.callmonitoring.config.CallMonitoringProperties;
import dev.diegosaurus.cimb.callmonitoring.domain.SentimentBucket;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchRequest;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchResponse;
import dev.diegosaurus.cimb.callmonitoring.exception.InvalidSortColumnException;
import dev.diegosaurus.cimb.callmonitoring.service.CallMonitoringService;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/api/call-monitoring")
@Validated
public class CallMonitoringController {

    private static final Set<String> ALLOWED_SORTS =
            Set.of("callId", "callTimestamp", "csAgentName", "customerName", "sentimentScore");

    private final CallMonitoringService service;
    private final CallMonitoringProperties properties;

    public CallMonitoringController(CallMonitoringService service,
                                    CallMonitoringProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    @GetMapping
    public ResponseEntity<CallMonitoringSearchResponse> search(
            @RequestParam(required = false) @Size(max = 128) String search,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String sentiment,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int size,
            @RequestParam(defaultValue = "callTimestamp") String sortBy,
            @RequestParam(defaultValue = "asc") @Pattern(
                    regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE) String direction
    ) {
        String normalizedSortBy = sortBy == null || sortBy.isBlank() ? "callTimestamp" : sortBy;
        if (!ALLOWED_SORTS.contains(normalizedSortBy)) {
            throw new InvalidSortColumnException(normalizedSortBy);
        }

        int effectiveSize = size <= 0 ? properties.getDefaultPageSize() : size;

        SentimentBucket bucket = SentimentBucket.parse(sentiment);

        CallMonitoringSearchRequest req = CallMonitoringSearchRequest.builder()
                .search(search)
                .startDate(startDate)
                .endDate(endDate)
                .sentimentBucket(bucket)
                .page(page)
                .size(effectiveSize)
                .sortBy(normalizedSortBy)
                .direction(direction)
                .build();

        return ResponseEntity.ok(service.search(req));
    }
}
