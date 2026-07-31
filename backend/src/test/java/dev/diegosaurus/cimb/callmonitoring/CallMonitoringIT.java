package dev.diegosaurus.cimb.callmonitoring;

import dev.diegosaurus.cimb.callmonitoring.domain.SentimentBucket;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchRequest;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchResponse;
import dev.diegosaurus.cimb.callmonitoring.exception.InvalidDateRangeException;
import dev.diegosaurus.cimb.callmonitoring.service.CallMonitoringService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

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
        CallMonitoringSearchResponse response = service.search(
                new CallMonitoringSearchRequest(null, null, null, null, 0, 5,
                        "callTimestamp", "asc"));

        assertThat(response.items()).hasSize(5);
        assertThat(response.totalElements()).isGreaterThanOrEqualTo(10);
    }

    @Test
    void filtersByBelow70AgainstPostgres() {
        CallMonitoringSearchResponse response = service.search(
                new CallMonitoringSearchRequest(null, null, null,
                        SentimentBucket.BELOW_70, 0, 5,
                        "callTimestamp", "asc"));

        assertThat(response.items()).allSatisfy(item ->
                assertThat(item.sentimentScore()).isLessThan(new java.math.BigDecimal("70")));
    }

    @Test
    void filtersByDateRangeAgainstPostgres() {
        CallMonitoringSearchResponse response = service.search(
                new CallMonitoringSearchRequest(null,
                        LocalDate.of(2025, 5, 1),
                        LocalDate.of(2025, 5, 31),
                        null, 0, 5, "callTimestamp", "asc"));

        assertThat(response.items()).allSatisfy(item ->
                assertThat(item.callTimestamp().toLocalDate())
                        .isBetween(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 31)));
    }

    @Test
    void rejectsInvertedDateRange() {
        assertThatThrownBy(() -> service.search(
                new CallMonitoringSearchRequest(null,
                        LocalDate.of(2025, 5, 10),
                        LocalDate.of(2025, 5, 1),
                        null, 0, 5, "callTimestamp", "asc")))
                .isInstanceOf(InvalidDateRangeException.class);
    }
}
