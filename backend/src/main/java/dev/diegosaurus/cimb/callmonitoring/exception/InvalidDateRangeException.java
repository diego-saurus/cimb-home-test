package dev.diegosaurus.cimb.callmonitoring.exception;

import java.time.LocalDate;

public class InvalidDateRangeException extends RuntimeException {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public InvalidDateRangeException(LocalDate startDate, LocalDate endDate) {
        super("startDate must be on or before endDate (start=" + startDate + ", end=" + endDate + ")");
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}
