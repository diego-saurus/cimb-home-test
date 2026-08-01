package dev.diegosaurus.cimb.callmonitoring.exception;

public class InvalidSortColumnException extends RuntimeException {
    public InvalidSortColumnException(String column) {
        super("Unknown sort column: " + column);
    }
}
