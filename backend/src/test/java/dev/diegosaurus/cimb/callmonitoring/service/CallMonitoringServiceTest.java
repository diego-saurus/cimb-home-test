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
import org.springframework.data.domain.Page;

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

        Page<CallMonitoringSearchResponse> page0 = service.search(defaultRequest());
        Page<CallMonitoringSearchResponse> page1 = service.search(pageRequest(1));
        Page<CallMonitoringSearchResponse> page2 = service.search(pageRequest(2));

        assertThat(page0.getContent()).hasSize(5);
        assertThat(page1.getContent()).hasSize(5);
        assertThat(page2.getContent()).hasSize(2);
        assertThat(page0.getTotalElements()).isEqualTo(12);
        assertThat(page0.getTotalPages()).isEqualTo(3);
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

        assertThat(service.search(asc).getContent()).extracting("callId").containsExactly("A", "B", "C");
        assertThat(service.search(desc).getContent()).extracting("callId").containsExactly("C", "B", "A");
    }

    @Test
    void returnsEmptyPageWhenNoRecords() {
        Page<CallMonitoringSearchResponse> response = service.search(defaultRequest());
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isZero();
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

    @Test
    void sortsByCustomerNameMappedFromCsAgentName() {
        seed("A", LocalDate.of(2025, 1, 1), "cs", "Zara", 80);
        seed("B", LocalDate.of(2025, 1, 2), "cs", "Andi", 80);
        seed("C", LocalDate.of(2025, 1, 3), "cs", "Mira", 80);

        CallMonitoringSearchRequest req = CallMonitoringSearchRequest.builder()
                .page(0).size(5).sortBy("customerName").direction("asc").build();

        Page<CallMonitoringSearchResponse> page = service.search(req);
        assertThat(page.getContent()).extracting("callId").containsExactly("B", "C", "A");
    }

    @Test
    void sortsByCsAgentNameMappedFromCsAgentName() {
        seed("A", LocalDate.of(2025, 1, 1), "Zara", "cust", 80);
        seed("B", LocalDate.of(2025, 1, 2), "Andi", "cust", 80);
        seed("C", LocalDate.of(2025, 1, 3), "Mira", "cust", 80);

        CallMonitoringSearchRequest req = CallMonitoringSearchRequest.builder()
                .page(0).size(5).sortBy("csAgentName").direction("asc").build();

        Page<CallMonitoringSearchResponse> page = service.search(req);
        assertThat(page.getContent()).extracting("callId").containsExactly("B", "C", "A");
    }

    private void seed(String callId, LocalDate date, String cs, String cust, int score) {
        CsAgent agent = CsAgent.builder().id(1).csName(cs).build();
        Customer customer = Customer.builder().id(1).customerName(cust).build();
        repo.save(new CallMonitoring(callId, date.atStartOfDay(),
                agent, customer, BigDecimal.valueOf(score)));
    }
}
