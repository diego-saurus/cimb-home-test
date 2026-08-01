package dev.diegosaurus.cimb.callmonitoring.service;

import dev.diegosaurus.cimb.callmonitoring.domain.CallMonitoring;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchRequest;
import dev.diegosaurus.cimb.callmonitoring.dto.CallMonitoringSearchResponse;
import dev.diegosaurus.cimb.callmonitoring.exception.InvalidDateRangeException;
import dev.diegosaurus.cimb.callmonitoring.repository.CallMonitoringRepository;
import dev.diegosaurus.cimb.callmonitoring.repository.CallMonitoringSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CallMonitoringService {

    private static final Logger log = LoggerFactory.getLogger(CallMonitoringService.class);
    private static final int DEFAULT_PAGE_SIZE = 5;

    private final CallMonitoringRepository repository;

    public CallMonitoringService(CallMonitoringRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<CallMonitoringSearchResponse> search(CallMonitoringSearchRequest req) {
        validateDateRange(req);

        int page = Math.max(req.getPage(), 0);
        int size = req.getSize() <= 0 ? DEFAULT_PAGE_SIZE : req.getSize();
        Sort.Direction dir = Sort.Direction.fromOptionalString(req.getDirection())
                .orElse(Sort.Direction.ASC);
        String rawSortBy = req.getSortBy() == null || req.getSortBy().isBlank()
                ? "callTimestamp" : req.getSortBy();
        String sortBy = toJpaProperty(rawSortBy);

        log.info("Searching call monitoring: search='{}', start={}, end={}, bucket={}, page={}, size={}, sortBy={}, dir={}",
                req.getSearch(), req.getStartDate(), req.getEndDate(),
                req.getSentimentBucket(), page, size, sortBy, dir);

        Specification<CallMonitoring> spec = CallMonitoringSpecifications.from(req);
        return repository.findAll(spec, PageRequest.of(page, size, Sort.by(dir, sortBy)))
                .map(this::toItem);
    }

    private static String toJpaProperty(String sortBy) {
        return switch (sortBy) {
            case "csAgentName" -> "csAgent.csName";
            case "customerName" -> "customer.customerName";
            default -> sortBy;
        };
    }

    private void validateDateRange(CallMonitoringSearchRequest req) {
        if (req.getStartDate() != null && req.getEndDate() != null
                && req.getStartDate().isAfter(req.getEndDate())) {
            throw new InvalidDateRangeException(req.getStartDate(), req.getEndDate());
        }
    }

    private CallMonitoringSearchResponse toItem(CallMonitoring cm) {
        return CallMonitoringSearchResponse.builder()
                .id(cm.getId())
                .callId(cm.getCallId())
                .callTimestamp(cm.getCallTimestamp())
                .csAgentName(cm.getCsAgent().getCsName())
                .customerName(cm.getCustomer().getCustomerName())
                .sentimentScore(cm.getSentimentScore())
                .build();
    }
}
