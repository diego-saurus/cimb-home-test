package dev.diegosaurus.cimb.callmonitoring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "call_monitorings")
public class CallMonitoring {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(name = "call_id", nullable = false, unique = true, length = 50)
    private String callId;

    @Getter
    @Column(name = "call_timestamp", nullable = false)
    private LocalDateTime callTimestamp;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cs_agent_id", nullable = false)
    private CsAgent csAgent;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Getter
    @Column(name = "sentiment_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal sentimentScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CallMonitoring() {
    }

    public CallMonitoring(String callId, LocalDateTime callTimestamp,
                          CsAgent csAgent, Customer customer, BigDecimal sentimentScore) {
        this.callId = callId;
        this.callTimestamp = callTimestamp;
        this.csAgent = csAgent;
        this.customer = customer;
        this.sentimentScore = sentimentScore;
    }

    @jakarta.persistence.PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @jakarta.persistence.PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallMonitoring other)) return false;
        return Objects.equals(callId, other.callId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callId);
    }
}
