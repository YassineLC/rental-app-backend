package com.rental.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            try {
                Long userId = jwtTokenProvider.extractUserId(token);
                String role = jwtTokenProvider.extractRole(token);

                HttpServletRequest wrappedRequest = new HeaderInjectionRequestWrapper(request, userId, role);
                filterChain.doFilter(wrappedRequest, response);
                return;
            } catch (Exception e) {
                logger.debug("JWT token extraction failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private static class HeaderInjectionRequestWrapper extends HttpServletRequestWrapper {
        private final Long userId;
        private final String role;
        private final Map<String, String> headerMap;

        public HeaderInjectionRequestWrapper(HttpServletRequest request, Long userId, String role) {
            super(request);
            this.userId = userId;
            this.role = role;
            this.headerMap = new HashMap<>();
            this.headerMap.put("X-User-Id", userId.toString());
            this.headerMap.put("X-User-Role", role);
        }

        @Override
        public String getHeader(String name) {
            String value = headerMap.get(name);
            if (value != null) {
                return value;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String value = headerMap.get(name);
            if (value != null) {
                Vector<String> vector = new Vector<>();
                vector.add(value);
                return vector.elements();
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Vector<String> vector = new Vector<>(headerMap.keySet());
            Enumeration<String> parentHeaders = super.getHeaderNames();
            while (parentHeaders.hasMoreElements()) {
                String headerName = parentHeaders.nextElement();
                if (!headerMap.containsKey(headerName)) {
                    vector.add(headerName);
                }
            }
            return vector.elements();
        }
    }
}

