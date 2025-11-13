package com.test_project.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String DEFAULT_VALIDATION_MESSAGE = "The given data was invalid.";

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, List<String>> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors
                    .computeIfAbsent(error.getField(), key -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        }

        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, DEFAULT_VALIDATION_MESSAGE, request, fieldErrors);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, List<String>> errors = Map.of(
                "body",
                List.of("Request body is missing or malformed.")
        );

        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, DEFAULT_VALIDATION_MESSAGE, request, errors);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, List<String>> errors = Map.of(
                "resource",
                List.of(ex.getHttpMethod() + " " + ex.getRequestURL())
        );

        return buildResponse(HttpStatus.NOT_FOUND, "The requested resource was not found.", request, errors);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        List<String> supported = ex.getSupportedHttpMethods() == null
                ? List.of()
                : ex.getSupportedHttpMethods().stream()
                .map(HttpMethod::name)
                .sorted()
                .collect(Collectors.toList());

        Map<String, List<String>> errors = new LinkedHashMap<>();
        errors.put("method", List.of(Objects.requireNonNullElse(ex.getMethod(), "UNKNOWN")));
        errors.put("supported", supported.isEmpty() ? List.of("none") : supported);

        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not supported for this endpoint.", request, errors);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        List<String> supported = ex.getSupportedMediaTypes() == null
                ? List.of()
                : ex.getSupportedMediaTypes().stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        Map<String, List<String>> errors = new LinkedHashMap<>();
        errors.put("contentType", List.of(ex.getContentType() == null ? "UNKNOWN" : ex.getContentType().toString()));
        errors.put("supported", supported.isEmpty() ? List.of("none") : supported);

        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content type is not supported.", request, errors);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        Map<String, List<String>> errors = Map.of(
                ex.getParameterName(),
                List.of("This request parameter is required.")
        );

        return buildResponse(HttpStatus.BAD_REQUEST, "Required parameter is missing.", request, errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Map<String, List<String>> errors = Map.of(
                ex.getName(),
                List.of("Expected type " + (ex.getRequiredType() == null ? "unknown" : ex.getRequiredType().getSimpleName()))
        );

        return buildResponse(HttpStatus.BAD_REQUEST, "One or more parameters have invalid values.", request, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String field = violation.getPropertyPath() == null
                    ? "value"
                    : violation.getPropertyPath().toString();

            errors.computeIfAbsent(field, key -> new ArrayList<>())
                    .add(violation.getMessage());
        }

        return buildResponse(HttpStatus.BAD_REQUEST, DEFAULT_VALIDATION_MESSAGE, request, errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "The requested operation violates a data integrity rule.", request);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleIllegalArguments(RuntimeException ex, WebRequest request) {
        String message = ex.getMessage() == null ? "The request could not be processed." : ex.getMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(AuthenticationException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Authentication failed. Please sign in again.", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "You are not allowed to access this resource.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaught(Exception ex, WebRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", request);
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message, WebRequest request) {
        return buildResponse(status, message, request, null);
    }

    private ResponseEntity<Object> buildResponse(
            HttpStatus status,
            String message,
            WebRequest request,
            Map<String, ?> details
    ) {
        ApiErrorResponse body = ApiErrorResponse.of(status, message, resolvePath(request), details);
        return ResponseEntity.status(status).body(body);
    }

    private String resolvePath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "";
    }
}
