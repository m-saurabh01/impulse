package com.wipro.iaf.email.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter to block login attempts from locked-out IP addresses.
 * 
 * <p>Intercepts POST requests to /login and checks if the client IP
 * is currently blocked due to too many failed login attempts.
 * Runs at highest precedence to block requests early.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see LoginRateLimiter
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final LoginRateLimiter rateLimiter;

    /**
     * Constructs the filter with the rate limiter dependency.
     * 
     * @param rateLimiter the rate limiter to check block status
     */
    public LoginRateLimitFilter(LoginRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    /**
     * Filters incoming requests to check for locked-out IPs on login attempts.
     * 
     * <p>Only checks POST requests to /login. If the IP is blocked,
     * redirects to login page with an error message.</p>
     * 
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if filtering fails
     * @throws IOException      if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Only check for login POST requests
        if ("POST".equalsIgnoreCase(request.getMethod()) && "/login".equals(request.getRequestURI())) {
            String ipAddress = getClientIP(request);
            
            if (rateLimiter.isBlocked(ipAddress)) {
                long remainingSeconds = rateLimiter.getRemainingLockoutSeconds(ipAddress);
                long remainingMinutes = remainingSeconds / 60 + 1;
                
                request.getSession().setAttribute("loginError", 
                        "Too many failed attempts. Please try again in " + remainingMinutes + " minute(s).");
                response.sendRedirect(request.getContextPath() + "/login?error=locked");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the client IP address from the request.
     * 
     * <p>Handles X-Forwarded-For header for clients behind proxies.</p>
     * 
     * @param request the HTTP request
     * @return the client IP address
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
