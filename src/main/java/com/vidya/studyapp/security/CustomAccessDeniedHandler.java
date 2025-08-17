package com.vidya.studyapp.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        System.out.println(" Access Denied Handler triggered");
        System.out.println(" Request path: " + request.getServletPath());
        System.out.println(" Request method: " + request.getMethod());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("User: " + auth.getName());
            System.out.println(" Authorities: " + auth.getAuthorities());
            System.out.println(" Authenticated: " + auth.isAuthenticated());
        } else {
            System.out.println(" No authentication found");
        }
        
        System.out.println(" Exception: " + accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Access denied: You don't have permission to perform this action.\", \"path\": \"" + request.getServletPath() + "\"}");
    }
}
