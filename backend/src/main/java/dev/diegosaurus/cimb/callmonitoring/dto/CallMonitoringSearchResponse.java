package dev.diegosaurus.cimb.callmonitoring.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CallMonitoringSearchResponse {
    Long id;
    String callId;
    LocalDateTime callTimestamp;
    String csAgentName;
    String customerName;
    BigDecimal sentimentScore;
}
