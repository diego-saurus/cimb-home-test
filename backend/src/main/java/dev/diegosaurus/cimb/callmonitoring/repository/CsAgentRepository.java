package dev.diegosaurus.cimb.callmonitoring.repository;

import dev.diegosaurus.cimb.callmonitoring.domain.CsAgent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsAgentRepository extends JpaRepository<CsAgent, Integer> {
}
