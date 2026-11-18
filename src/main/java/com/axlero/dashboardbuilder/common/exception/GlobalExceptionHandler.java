package com.axlero.dashboardbuilder.common.exception;

import com.axlero.dashboardbuilder.common.response.ApiErrorResponse;
import com.axlero.dashboardbuilder.common.response.ApiErrorResponse.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * Converts exceptions into consistent JSON error responses.
 * Controllers and services just throw exceptions; this class decides the HTTP status.
 *
 * <p>Logging policy: 5xx are logged at ERROR with the stack trace, client errors (4xx) at WARN as a
 * single line, and plain "not found" at DEBUG. Request bodies and exception details that may
 * contain user data are never logged.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex,
                                                           HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateResourceException ex,
                                                            HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    /** Fired when a @Valid request body fails validation. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                             HttpServletRequest request) {
        List<FieldValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldValidationError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
    }

    /** Malformed JSON or wrong data types in the body. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                             HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Malformed or unreadable request body", request, null);
    }

    /** E.g. /api/workspaces/not-a-uuid. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                               HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + ex.getName() + "'",
                request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request, null);
    }

    /** A Content-Type the endpoint does not accept, e.g. text/plain sent to a JSON endpoint. */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported content type", request, null);
    }

    /** Unknown URL. Spring Boot 3.2+ throws NoResourceFoundException; older setups NoHandlerFoundException. */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleRouteNotFound(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Resource not found", request, null);
    }

    /**
     * A database constraint was violated, e.g. two concurrent requests inserting the same unique value.
     * Only the constraint name is logged: the database message can contain the offending data.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                HttpServletRequest request) {
        String constraint = ex.getCause() instanceof ConstraintViolationException violation
                ? violation.getConstraintName()
                : "unknown";
        log.warn("Data integrity violation on {} {} (constraint: {})",
                request.getMethod(), request.getRequestURI(), constraint);
        return build(HttpStatus.CONFLICT, "The request conflicts with existing data", request, null);
    }

    /** Safety net: log the details, but never leak internals to the client. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, null);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message,
                                                   HttpServletRequest request,
                                                   List<FieldValidationError> fieldErrors) {
        logClientError(status, message, request);
        ApiErrorResponse body = ApiErrorResponse.of(
                status.value(), status.getReasonPhrase(), message, request.getRequestURI(), fieldErrors);
        return ResponseEntity.status(status).body(body);
    }

    /** 5xx are logged by the handlers that raise them (with the stack trace); this covers 4xx. */
    private void logClientError(HttpStatus status, String message, HttpServletRequest request) {
        if (status == HttpStatus.NOT_FOUND) {
            log.debug("{} {} -> {} {}", request.getMethod(), request.getRequestURI(), status.value(), message);
        } else if (status.is4xxClientError()) {
            log.warn("{} {} -> {} {}", request.getMethod(), request.getRequestURI(), status.value(), message);
        }
    }
}