package com.rental.property.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

                // Add headers for downstream processing
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

    private static class HeaderInjectionRequestWrapper extends org.springframework.web.util.ContentCachingRequestWrapper {
        private final Long userId;
        private final String role;

        public HeaderInjectionRequestWrapper(HttpServletRequest request, Long userId, String role) {
            super(request);
            this.userId = userId;
            this.role = role;
        }

        @Override
        public String getHeader(String name) {
            if ("X-User-Id".equalsIgnoreCase(name)) {
                return userId.toString();
            }
            if ("X-User-Role".equalsIgnoreCase(name)) {
                return role;
            }
            return super.getHeader(name);
        }
    }
}
