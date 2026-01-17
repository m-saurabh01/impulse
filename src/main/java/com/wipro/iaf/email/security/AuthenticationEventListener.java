package com.wipro.iaf.email.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Listens for Spring Security authentication events to integrate with rate limiting.
 * 
 * <p>Records failed login attempts and clears attempt history on successful
 * authentication. Works with {@link LoginRateLimiter} to prevent brute force attacks.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see LoginRateLimiter
 */
@Component
public class AuthenticationEventListener {

    private final LoginRateLimiter rateLimiter;

    /**
     * Constructs the event listener with rate limiter dependency.
     * 
     * @param rateLimiter the rate limiter for tracking attempts
     */
    public AuthenticationEventListener(LoginRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    /**
     * Handles failed authentication events.
     * 
     * <p>Records the failed attempt in the rate limiter to track
     * potential brute force attacks.</p>
     * 
     * @param event the authentication failure event
     */
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String ipAddress = getClientIP();
        if (ipAddress != null) {
            rateLimiter.recordFailedAttempt(ipAddress);
        }
    }

    /**
     * Handles successful authentication events.
     * 
     * <p>Clears the attempt history for the IP to reset the
     * rate limiting counter after successful login.</p>
     * 
     * @param event the authentication success event
     */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String ipAddress = getClientIP();
        if (ipAddress != null) {
            rateLimiter.clearAttempts(ipAddress);
        }
    }

    /**
     * Extracts the client IP address from the current request context.
     * 
     * <p>Handles X-Forwarded-For header for clients behind proxies.</p>
     * 
     * @return the client IP address, or null if not available
     */
    private String getClientIP() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
