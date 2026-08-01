package dev.diegosaurus.cimb.callmonitoring;

import dev.diegosaurus.cimb.callmonitoring.domain.SentimentBucket;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchRequest;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchResponse;
import dev.diegosaurus.cimb.callmonitoring.exception.InvalidDateRangeException;
import dev.diegosaurus.cimb.callmonitoring.service.CallMonitoringService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test against the live PostgreSQL instance configured via the
 * `integrationtest` profile. Requires DB_URL, DB_USERNAME, DB_PASSWORD env vars,
 * or the defaults baked into application-integrationtest.yml.
 */
@SpringBootTest
@ActiveProfiles("integrationtest")
class CallMonitoringIT {

    @Autowired
    CallMonitoringService service;

    @Test
    void returnsPagedResultsAgainstPostgres() {
        Page<CallMonitoringSearchResponse> response = service.search(
                CallMonitoringSearchRequest.builder()
                        .page(0).size(5).sortBy("callTimestamp").direction("asc").build());

        assertThat(response.getContent()).hasSize(5);
        assertThat(response.getTotalElements()).isGreaterThanOrEqualTo(200);
    }

    @Test
    void filtersByBelow70AgainstPostgres() {
        Page<CallMonitoringSearchResponse> response = service.search(
                CallMonitoringSearchRequest.builder()
                        .sentimentBucket(SentimentBucket.BELOW_70)
                        .page(0).size(5).sortBy("callTimestamp").direction("asc").build());

        assertThat(response.getContent()).allSatisfy(item ->
                assertThat(item.getSentimentScore()).isLessThan(new BigDecimal("70")));
    }

    @Test
    void filtersByDateRangeAgainstPostgres() {
        Page<CallMonitoringSearchResponse> response = service.search(
                CallMonitoringSearchRequest.builder()
                        .startDate(LocalDate.of(2026, 5, 1))
                        .endDate(LocalDate.of(2026, 5, 31))
                        .page(0).size(5).sortBy("callTimestamp").direction("asc").build());

        assertThat(response.getContent()).allSatisfy(item ->
                assertThat(item.getCallTimestamp().toLocalDate())
                        .isBetween(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31)));
    }

    @Test
    void rejectsInvertedDateRange() {
        assertThatThrownBy(() -> service.search(
                CallMonitoringSearchRequest.builder()
                        .startDate(LocalDate.of(2026, 5, 10))
                        .endDate(LocalDate.of(2026, 5, 1))
                        .page(0).size(5).sortBy("callTimestamp").direction("asc").build()))
                .isInstanceOf(InvalidDateRangeException.class);
    }
}
