package dev.diegosaurus.cimb.callmonitoring.repository;

import dev.diegosaurus.cimb.callmonitoring.domain.CallMonitoring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CallMonitoringRepository {
    Page<CallMonitoring> findAll(Specification<CallMonitoring> spec, Pageable pageable);
}
