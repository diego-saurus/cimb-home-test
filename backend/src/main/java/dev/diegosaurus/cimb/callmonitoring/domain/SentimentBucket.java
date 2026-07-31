package dev.diegosaurus.cimb.callmonitoring.domain;

public enum SentimentBucket {
    BELOW_70,
    AT_OR_ABOVE_70;

    public static SentimentBucket parse(String raw) {
        if (raw == null || raw.isBlank()) return null;
        return SentimentBucket.valueOf(raw.trim().toUpperCase());
    }
}
