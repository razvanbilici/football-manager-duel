package com.football.exception;

import com.football.dto.MessageResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- @Valid on @RequestBody ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("status", 400);
        errors.put("error", "Validation failed");
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        errors.put("fields", fieldErrors);
        return ResponseEntity.badRequest().body(errors);
    }

    // ---- @Validated on @RequestParam / @PathVariable ----
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessageResponse> constraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Invalid request parameters");
        log.warn("Constraint violation: {}", msg);
        return ResponseEntity.badRequest().body(new MessageResponse(msg));
    }

    // ---- Wrong type in a path/query param, e.g. GET /players/abc ----
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MessageResponse> typeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
        log.warn(msg);
        return ResponseEntity.badRequest().body(new MessageResponse(msg));
    }

    // ---- Required query param missing ----
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<MessageResponse> missingParam(MissingServletRequestParameterException ex) {
        log.warn("Missing parameter: {}", ex.getParameterName());
        return ResponseEntity.badRequest()
                .body(new MessageResponse("Missing required parameter: " + ex.getParameterName()));
    }

    // ---- Malformed / unreadable JSON body ----
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageResponse> notReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed request body: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new MessageResponse("Malformed JSON request body"));
    }

    // ---- Wrong HTTP verb / content type ----
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<MessageResponse> methodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<MessageResponse> mediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new MessageResponse(ex.getMessage()));
    }

    // ---- Spring Security (e.g. thrown by @PreAuthorize during controller execution) ----
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<MessageResponse> authentication(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalidd credentials"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageResponse> accessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new MessageResponse("You do not have permission to perform this action"));
    }

    // ---- Your domain exceptions ----
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> notFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<MessageResponse> badRequest(BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<MessageResponse> forbidden(ForbiddenException ex) {
        log.warn("Forbidden: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(ex.getMessage()));
    }

    // ---- Common unchecked exceptions from service/business code ----
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> illegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<MessageResponse> illegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(ex.getMessage()));
    }

    // ---- Database constraint violations (SQLite) ----
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageResponse> dataIntegrity(DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : null;
        log.warn("Data integrity violation: {}", msg);

        if (msg != null && msg.contains("NOT NULL")) {
            return ResponseEntity.badRequest().body(new MessageResponse("A required field is missing"));
        }
        if (msg != null && msg.contains("UNIQUE")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("This record already exists"));
        }
        if (msg != null && msg.contains("FOREIGN KEY")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("This action references a record that doesn't exist or is still in use"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Operation violates data constraints"));
    }

    // ---- Absolute last resort — catches anything not handled above ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> generic(Exception ex) {
        String refId = UUID.randomUUID().toString().substring(0, 8);
        log.error("Unhandled exception [ref={}]", refId, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Something went wrong on our end (ref: " + refId + "). Please try again."));
    }
}