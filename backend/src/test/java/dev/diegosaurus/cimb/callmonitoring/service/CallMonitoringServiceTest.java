package dev.diegosaurus.cimb.callmonitoring.service;

import dev.diegosaurus.cimb.callmonitoring.domain.CallMonitoring;
import dev.diegosaurus.cimb.callmonitoring.domain.CsAgent;
import dev.diegosaurus.cimb.callmonitoring.domain.Customer;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchRequest;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchResponse;
import dev.diegosaurus.cimb.callmonitoring.exception.InvalidDateRangeException;
import dev.diegosaurus.cimb.callmonitoring.repository.InMemoryCallMonitoringRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CallMonitoringServiceTest {

    private final InMemoryCallMonitoringRepository repo = new InMemoryCallMonitoringRepository();
    private final CallMonitoringService service = new CallMonitoringService(repo);

    private static CallMonitoringSearchRequest defaultRequest() {
        return CallMonitoringSearchRequest.builder()
                .page(0).size(5).sortBy("callTimestamp").direction("asc").build();
    }

    private static CallMonitoringSearchRequest pageRequest(int page) {
        return CallMonitoringSearchRequest.builder()
                .page(page).size(5).sortBy("callTimestamp").direction("asc").build();
    }

    @Test
    void returnsPagedResultsByDefaultPageSizeOfFive() {
        for (int i = 1; i <= 12; i++) {
            seed("C" + (1000 + i), LocalDate.now().minusDays(i), "cs" + i, "cust" + i, 50 + i);
        }

        CallMonitoringSearchResponse page0 = service.search(defaultRequest());
        CallMonitoringSearchResponse page1 = service.search(pageRequest(1));
        CallMonitoringSearchResponse page2 = service.search(pageRequest(2));

        assertThat(page0.items()).hasSize(5);
        assertThat(page1.items()).hasSize(5);
        assertThat(page2.items()).hasSize(2);
        assertThat(page0.totalElements()).isEqualTo(12);
        assertThat(page0.totalPages()).isEqualTo(3);
    }

    @Test
    void sortsAscendingAndDescending() {
        seed("A", LocalDate.of(2025, 1, 1), "a", "b", 80);
        seed("B", LocalDate.of(2025, 1, 2), "a", "b", 80);
        seed("C", LocalDate.of(2025, 1, 3), "a", "b", 80);

        CallMonitoringSearchRequest asc = CallMonitoringSearchRequest.builder()
                .page(0).size(5).sortBy("callId").direction("asc").build();
        CallMonitoringSearchRequest desc = CallMonitoringSearchRequest.builder()
                .page(0).size(5).sortBy("callId").direction("desc").build();

        assertThat(service.search(asc).items()).extracting("callId").containsExactly("A", "B", "C");
        assertThat(service.search(desc).items()).extracting("callId").containsExactly("C", "B", "A");
    }

    @Test
    void returnsEmptyPageWhenNoRecords() {
        CallMonitoringSearchResponse response = service.search(defaultRequest());
        assertThat(response.items()).isEmpty();
        assertThat(response.totalElements()).isZero();
        assertThat(response.emptyStateMessage()).isNotBlank();
    }

    @Test
    void rejectsStartAfterEndWithInvalidRangeException() {
        CallMonitoringSearchRequest req = CallMonitoringSearchRequest.builder()
                .startDate(LocalDate.of(2025, 5, 10))
                .endDate(LocalDate.of(2025, 5, 1))
                .page(0).size(5).sortBy("callTimestamp").direction("asc")
                .build();

        assertThatThrownBy(() -> service.search(req))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    private void seed(String callId, LocalDate date, String cs, String cust, int score) {
        CsAgent agent = CsAgent.builder().id(1).csName(cs).build();
        Customer customer = Customer.builder().id(1).customerName(cust).build();
        repo.save(new CallMonitoring(callId, date.atStartOfDay(),
                agent, customer, BigDecimal.valueOf(score)));
    }
}
