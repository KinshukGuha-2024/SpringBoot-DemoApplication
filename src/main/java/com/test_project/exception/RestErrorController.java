package com.test_project.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class RestErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public RestErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping
    public ResponseEntity<ApiErrorResponse> error(HttpServletRequest request) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.defaults().including(
                        ErrorAttributeOptions.Include.MESSAGE
                )
        );

        int statusCode = (int) attributes.getOrDefault("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        HttpStatus status = Objects.requireNonNullElse(HttpStatus.resolve(statusCode), HttpStatus.INTERNAL_SERVER_ERROR);
        String messageFromAttributes = (String) attributes.getOrDefault("message", "");
        String message = resolveMessage(status, messageFromAttributes);

        ApiErrorResponse body = ApiErrorResponse.of(status, message, request.getRequestURI());

        return ResponseEntity.status(status).body(body);
    }

    private String defaultMessage(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> "The requested resource was not found.";
            case METHOD_NOT_ALLOWED -> "HTTP method not supported for this endpoint.";
            case FORBIDDEN -> "You are not allowed to access this resource.";
            case UNAUTHORIZED -> "Authentication failed. Please sign in again.";
            default -> "An unexpected error occurred.";
        };
    }

    private String resolveMessage(HttpStatus status, String messageFromAttributes) {
        if (messageFromAttributes == null || messageFromAttributes.isBlank() || "No message available".equals(messageFromAttributes)) {
            return defaultMessage(status);
        }
        return messageFromAttributes;
    }
}
