package dev.diegosaurus.cimb.callmonitoring.repository;

import dev.diegosaurus.cimb.callmonitoring.domain.CallMonitoring;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchRequest;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class CallMonitoringSpecifications {

    private static final BigDecimal PASS_THRESHOLD = new BigDecimal("70.00");

    private CallMonitoringSpecifications() { }

    public static Specification<CallMonitoring> from(CallMonitoringSearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (req.search() != null && !req.search().isBlank()) {
                String pattern = "%" + req.search().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("callId")), pattern),
                        cb.like(cb.lower(root.get("csAgent").get("csName")), pattern),
                        cb.like(cb.lower(root.get("customer").get("customerName")), pattern),
                        cb.like(cb.lower(root.get("callTimestamp").as(String.class)), pattern),
                        cb.like(cb.lower(root.get("sentimentScore").as(String.class)), pattern)
                ));
            }

            if (req.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("callTimestamp"),
                        req.startDate().atStartOfDay()));
            }

            if (req.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("callTimestamp"),
                        req.endDate().atTime(LocalTime.MAX)));
            }

            if (req.sentimentBucket() != null) {
                switch (req.sentimentBucket()) {
                    case BELOW_70 -> predicates.add(
                            cb.lessThan(root.get("sentimentScore"), PASS_THRESHOLD));
                    case AT_OR_ABOVE_70 -> predicates.add(
                            cb.greaterThanOrEqualTo(root.get("sentimentScore"), PASS_THRESHOLD));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
