package dev.diegosaurus.cimb.callmonitoring.repository;

import dev.diegosaurus.cimb.callmonitoring.domain.CallMonitoring;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface JpaCallMonitoringRepository
        extends JpaRepository<CallMonitoring, Long>,
                JpaSpecificationExecutor<CallMonitoring>,
                CallMonitoringRepository {

    @Override
    @EntityGraph(attributePaths = {"csAgent", "customer"})
    org.springframework.data.domain.Page<CallMonitoring> findAll(
            @Param("spec") Specification<CallMonitoring> spec,
            org.springframework.data.domain.Pageable pageable);
}
