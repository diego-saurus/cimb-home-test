package dev.diegosaurus.cimb.callmonitoring.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiError> handle(InvalidDateRangeException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError("invalid_date_range", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(InvalidSortColumnException.class)
    public ResponseEntity<ApiError> handle(InvalidSortColumnException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError("invalid_sort_column", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handle(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("validation failed");
        return ResponseEntity.badRequest()
                .body(new ApiError("validation_failed", msg, Instant.now()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handle(ConstraintViolationException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError("validation_failed", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handle(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError("bad_request", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handle(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("not_found", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handle(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ApiError("method_not_allowed", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle(Exception ex) {
        log.error("Unhandled error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("internal_error", "Unexpected error", Instant.now()));
    }

    public record ApiError(String error, String message, Instant timestamp) {
        public Map<String, Object> asMap() {
            return Map.of("error", error, "message", message, "timestamp", timestamp.toString());
        }
    }
}
