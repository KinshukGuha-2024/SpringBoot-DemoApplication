package com.test_project.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test_project.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public RestAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        ApiErrorResponse body = ApiErrorResponse.of(
                HttpStatus.FORBIDDEN,
                "You are not allowed to access this resource.",
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), body);
    }
}
