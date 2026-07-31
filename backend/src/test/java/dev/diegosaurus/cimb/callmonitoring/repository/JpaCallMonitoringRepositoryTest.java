package dev.diegosaurus.cimb.callmonitoring.repository;

import dev.diegosaurus.cimb.callmonitoring.domain.CallMonitoring;
import dev.diegosaurus.cimb.callmonitoring.domain.CsAgent;
import dev.diegosaurus.cimb.callmonitoring.domain.Customer;
import dev.diegosaurus.cimb.callmonitoring.domain.SentimentBucket;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class JpaCallMonitoringRepositoryTest {

    @Autowired
    private JpaCallMonitoringRepository repo;

    @Autowired
    private CsAgentRepository csAgentRepo;

    @Autowired
    private CustomerRepository customerRepo;

    private void seed(String id, LocalDate date, int score) {
        repo.saveAndFlush(new CallMonitoring(id, date.atStartOfDay(),
                csAgent(), customer(), BigDecimal.valueOf(score)));
    }

    private CsAgent csAgent() {
        return csAgentRepo.saveAndFlush(CsAgent.builder().csName("cs").build());
    }

    private Customer customer() {
        return customerRepo.saveAndFlush(Customer.builder().customerName("cust").build());
    }

    @Test
    void specSearchAndBelow70() {
        seed("A", LocalDate.of(2025, 5, 1), 50);
        seed("B", LocalDate.of(2025, 5, 2), 90);

        var page = repo.findAll(
                CallMonitoringSpecifications.from(
                        CallMonitoringSearchRequest.builderWithDefaults()
                                .sentimentBucket(SentimentBucket.BELOW_70).build()),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "callId")));

        assertThat(page.getContent()).extracting("callId").containsExactly("A");
    }

    @Test
    void specSearchWithDateRange() {
        seed("X1", LocalDate.of(2025, 5, 1), 80);
        seed("X2", LocalDate.of(2025, 5, 15), 80);
        seed("X3", LocalDate.of(2025, 6, 1), 80);

        var req = CallMonitoringSearchRequest.builderWithDefaults()
                .startDate(LocalDate.of(2025, 5, 1))
                .endDate(LocalDate.of(2025, 5, 31))
                .build();
        var page = repo.findAll(
                CallMonitoringSpecifications.from(req),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "callId")));

        assertThat(page.getContent()).extracting("callId").containsExactly("X1", "X2");
    }
}
