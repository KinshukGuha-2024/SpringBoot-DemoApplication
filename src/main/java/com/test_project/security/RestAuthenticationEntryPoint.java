package com.test_project.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test_project.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public RestAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ApiErrorResponse body = ApiErrorResponse.of(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed. Please sign in again.",
                request.getRequestURI()
        );

        writeBody(response, HttpStatus.UNAUTHORIZED, body);
    }

    private void writeBody(HttpServletResponse response, HttpStatus status, ApiErrorResponse body) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), body);
    }
}
