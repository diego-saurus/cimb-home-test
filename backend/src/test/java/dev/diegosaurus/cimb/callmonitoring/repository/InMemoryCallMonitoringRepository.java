package dev.diegosaurus.cimb.callmonitoring.repository;

import dev.diegosaurus.cimb.callmonitoring.domain.CallMonitoring;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class InMemoryCallMonitoringRepository implements CallMonitoringRepository {

    private final ConcurrentHashMap<Long, CallMonitoring> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public CallMonitoring save(CallMonitoring entity) {
        long id = entity.getId() != null ? entity.getId() : seq.getAndIncrement();
        CallMonitoring persisted = new CallMonitoring(entity.getCallId(),
                entity.getCallTimestamp(), entity.getCsAgent(), entity.getCustomer(),
                entity.getSentimentScore()) {
            @Override public Long getId() { return id; }
        };
        store.put(id, persisted);
        return persisted;
    }

    @Override
    public Page<CallMonitoring> findAll(Specification<CallMonitoring> spec, Pageable pageable) {
        List<CallMonitoring> all = new ArrayList<>(store.values());
        Sort.Order order = pageable.getSort().iterator().next();
        Comparator<CallMonitoring> cmp = comparator(order.getProperty());
        if (cmp != null) {
            all.sort(order.isDescending() ? cmp.reversed() : cmp);
        }
        int from = Math.min((int) pageable.getOffset(), all.size());
        int to = Math.min(from + pageable.getPageSize(), all.size());
        return new PageImpl<>(all.subList(from, to), pageable, all.size());
    }

    private Comparator<CallMonitoring> comparator(String sortBy) {
        return switch (sortBy) {
            case "callId" -> Comparator.comparing(CallMonitoring::getCallId);
            case "callTimestamp" -> Comparator.comparing(CallMonitoring::getCallTimestamp);
            case "csAgentName" -> Comparator.comparing(cm -> cm.getCsAgent().getCsName());
            case "customerName" -> Comparator.comparing(cm -> cm.getCustomer().getCustomerName());
            case "sentimentScore" -> Comparator.comparing(CallMonitoring::getSentimentScore);
            default -> null;
        };
    }
}
